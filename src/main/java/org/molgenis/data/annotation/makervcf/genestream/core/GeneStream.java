package org.molgenis.data.annotation.makervcf.genestream.core;

import com.sun.tools.javac.jvm.Gen;
import org.molgenis.data.annotation.makervcf.structs.Relevance;
import org.molgenis.data.annotation.makervcf.structs.RelevantVariant;

import java.util.*;

/**
 * Created by joeri on 6/29/16.
 *
 */
public abstract class GeneStream {

    private Iterator<RelevantVariant> relevantVariants;
    protected boolean verbose;

    public GeneStream(Iterator<RelevantVariant> relevantVariants, boolean verbose)
    {
        this.relevantVariants = relevantVariants;
        this.verbose = verbose;
    }

    public Iterator<RelevantVariant> go()
    {
        return new Iterator<RelevantVariant>(){

            RelevantVariant nextResult;
            Set<String> previousGenes;
            Set<String> currentGenes;

            HashMap<String, List<RelevantVariant>> variantBufferPerGene = new HashMap<>();
            List<RelevantVariant> variantBuffer = new ArrayList<>();
            Iterator<RelevantVariant> resultBatch;

            @Override
            public boolean hasNext() {
                try {

                    if(resultBatch != null && resultBatch.hasNext())
                    {
                        if(verbose){System.out.println("[GeneStream] Returning subsequent result of gene stream batch");}
                        nextResult = resultBatch.next();
                        return true;
                    }
                    else {

                        while (relevantVariants.hasNext())
                        {
                            // cleanup of result batch after previously flushed results
                            if (resultBatch != null) {
                                if (verbose) { System.out.println("[GeneStream] Cleanup by setting result batch to null"); }
                                resultBatch = null;
                            }

                            RelevantVariant rv = relevantVariants.next();
                            currentGenes = rv.getRelevantGenes();
                            if (verbose) { System.out.println("[GeneStream] Entering while, looking at a variant in gene " + currentGenes); }


                            // if the previously seen genes are fully disjoint from the current genes, start processing per gene and flush buffer
                            if (previousGenes != null && Collections.disjoint(previousGenes, currentGenes)) {
                                if (verbose) { System.out.println("[GeneStream] Executing the abstract perGene() function on " + previousGenes); }

                                // process per gene in abstract function
                                for (String gene : variantBufferPerGene.keySet()) {
                                    if (verbose) { System.out.println("[GeneStream] Processing gene "+gene+" having " + variantBufferPerGene.get(gene).size() + " variants"); }
                                    perGene(gene, variantBufferPerGene.get(gene));
                                }
                                // create shallow copy, so that we can add another variant to buffer after we instantiate the iterator
                                resultBatch = new ArrayList<>(variantBuffer).iterator();

                                //reset buffers
                                variantBuffer = new ArrayList<>();
                                variantBufferPerGene = new HashMap<>();

                            }

                            // add current variant to gene-specific buffer
                            for (Relevance rlv : rv.getRelevance()) {
                                String gene = rlv.getGene();
                                if (variantBufferPerGene.containsKey(gene)) {
                                    variantBufferPerGene.get(gene).add(rv);
                                } else {
                                    List<RelevantVariant> variants = new ArrayList<>();
                                    variants.add(rv);
                                    variantBufferPerGene.put(gene, variants);
                                }
                            }
                            // add variant to global buffer
                            variantBuffer.add(rv);

                            // cycle previous and current genes
                            previousGenes = currentGenes;

                            // if result batch ready, start streaming it out
                            if (resultBatch != null && resultBatch.hasNext()) {
                                if (verbose) { System.out.println("[GeneStream] Returning first result of gene stream batch"); }
                                nextResult = resultBatch.next();
                                return true;
                            } else {
                                //nothing to return for this gene after perGene(previousGene, variantsForGene)
                                //so we go straight to cleanup in the next iteration
                            }
                        }
                    }


                    //process the last remaining data before ending
                    if(variantBuffer.size() > 0)
                    {
                        if (verbose) { System.out.println("[GeneStream] Buffer has " + variantBuffer.size() + " variants left in " + variantBufferPerGene.keySet().toString()); }
                        for(String gene : variantBufferPerGene.keySet())
                        {
                            perGene(gene, variantBufferPerGene.get(gene));
                        }
                        resultBatch = new ArrayList<>(variantBuffer).iterator();
                        variantBuffer = new ArrayList<>();
                        variantBufferPerGene = new HashMap<>();
                        if(resultBatch.hasNext())
                        {
                            if(verbose){System.out.println("[GeneStream] Returning first of remaining variants");}
                            nextResult = resultBatch.next();
                            return true;
                        }
                    }

                    return false;
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public RelevantVariant next() {
                return nextResult;
            }
        };
    }

    public abstract void perGene(String gene, List<RelevantVariant> variantsPerGene) throws Exception;

}
