package net.sourceforge.texlipse.bibparser;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.texlipse.model.ParseErrorMessage;
import net.sourceforge.texlipse.model.ReferenceEntry;

import org.junit.Test;

public class BibParserTest {
	
	@Test
	public void tsetParseEmtyString() throws FileNotFoundException, IOException {
		BibParser parser = new BibParser(new StringReader(""));
		parser.getEntries();
		assertEquals(new ArrayList<ParseErrorMessage>(), parser.getErrors());
		assertEquals(new ArrayList<ParseErrorMessage>(), parser.getWarnings());
	}
	
	@Test
	public void testCheckForMissingIdentifiers() throws FileNotFoundException, IOException {
		BibParser parser = new BibParser(new StringReader("@ARTICLE{}"));
		
		parser.getEntries();

		assertEquals("expecting: identifier", parser.getErrors().get(0).getMsg());
	}
	
	@Test
	public void testCheckInvalidField() throws FileNotFoundException, IOException {
		BibParser parser = new BibParser(new StringReader("@ARTICLE{bibkey,asd={xy}}"));
		
		parser.getEntries();

		assertEquals("article bibkey is missing required field author", parser.getWarnings().get(0).getMsg());
	}
	
	@Test
	public void testCheckForMissingEquals() throws FileNotFoundException, IOException {
		BibParser parser = new BibParser(new StringReader("@ARTICLE{bibkey,title}"));
		
		parser.getEntries();

		assertEquals("expecting: '='", parser.getErrors().get(0).getMsg());
	}

	@Test
	public void testCheckForRequiredAuthorField() throws FileNotFoundException, IOException {
		BibParser parser = new BibParser(new StringReader("@ARTICLE{bibkey,title={Test}}"));
		
		parser.getEntries();

		assertEquals("article bibkey is missing required field author", parser.getWarnings().get(0).getMsg());
	}
	
	@Test
	public void testCorrectEntry() throws FileNotFoundException, IOException {
		BibParser parser = new BibParser(new StringReader("@ARTICLE{bibkey,title={Title},author={Author},journal={Test},year={2000}}"));
		List<ReferenceEntry> refs = new ArrayList<ReferenceEntry>();
		refs.add(new ReferenceEntry("bibkey"));
		
		List<ReferenceEntry> entries = parser.getEntries();

		assertEquals(new ArrayList<ParseErrorMessage>(), parser.getErrors());
		assertEquals(new ArrayList<ParseErrorMessage>(), parser.getWarnings());
		assertEquals(refs.size(), entries.size());
		assertEquals(1, entries.size());
		assertEquals(refs.get(0).key, entries.get(0).key);
	}
	
	@Test
	public void testEntryWithAdditionalField() throws FileNotFoundException, IOException {
		BibParser parser = new BibParser(new StringReader("@ARTICLE{bibkey,title={Title},author={Author},journal={Test},year={2000},fubar={yes}}"));
		List<ReferenceEntry> refs = new ArrayList<ReferenceEntry>();
		refs.add(new ReferenceEntry("bibkey"));
		
		List<ReferenceEntry> entries = parser.getEntries();

		assertEquals(new ArrayList<ParseErrorMessage>(), parser.getErrors());
		assertEquals(new ArrayList<ParseErrorMessage>(), parser.getWarnings());
		assertEquals(refs.size(), entries.size());
		assertEquals(1, entries.size());
		assertEquals(refs.get(0).key, entries.get(0).key);
		assertEquals("yes", entries.get(0).getField("fubar"));
	}

}
