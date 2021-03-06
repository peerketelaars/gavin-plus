package org.molgenis.data.annotation.makervcf;

import org.apache.commons.io.FileUtils;
import org.molgenis.data.annotation.makervcf.genestream.core.ConvertToGeneStream;
import org.molgenis.data.annotation.makervcf.genestream.impl.AddGeneFDR;
import org.molgenis.data.annotation.makervcf.positionalstream.DiscoverRelevantVariants;
import org.molgenis.data.annotation.makervcf.positionalstream.MatchVariantsToGenotypeAndInheritance;
import org.molgenis.data.annotation.makervcf.structs.RelevantVariant;
import org.molgenis.data.annotation.makervcf.util.HandleMissingCaddScores;
import org.springframework.util.FileCopyUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

import static org.testng.Assert.*;

public class BadCaddWebserviceFile extends Setup
{

	protected File inputVcfFile;
	protected File cgdFile;
	protected File fdrFile;

	@BeforeClass
	public void beforeClass() throws FileNotFoundException, IOException {

	}

	@Test
	public void testGzInsteadOfTsv() throws Exception
	{
		//assertTrue(false);

//		Main.main(new String[]{"-i " + File.createTempFile("tmp", "vcf")});

//		new Pipeline();
//
//		DiscoverRelevantVariants discover = new DiscoverRelevantVariants(inputVcfFile, gavinFile, clinvarFile, caddFile, null, HandleMissingCaddScores.Mode.ANALYSIS, false);
//		Iterator<RelevantVariant> match = new MatchVariantsToGenotypeAndInheritance(discover.findRelevantVariants(), cgdFile, new HashSet<String>(), false).go();


	}

	@Test
	public void testBadTsv() throws Exception
	{
	//	assertTrue(false);



	}

}
