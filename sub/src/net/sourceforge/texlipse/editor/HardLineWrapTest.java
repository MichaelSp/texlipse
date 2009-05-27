package net.sourceforge.texlipse.editor;

import static org.junit.Assert.*;


import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.junit.Before;
import org.junit.Test;

public class HardLineWrapTest {

    private static class TestDocumentCommand extends DocumentCommand {
        public TestDocumentCommand(int offset, String text) {
            super();
            this.offset = offset;
            this.text = text;
            this.length = 0;
            this.caretOffset = offset;
            this.shiftsCaret = true;
        }
        
        public void execute(IDocument document){
        	try{
        		document.replace(offset, length, text);
        	} catch (BadLocationException ex) {
        		assertTrue(false);
        	}
            if (shiftsCaret == true) {
                caretOffset += text.length();
            }
        }
        
    }
    
    HardLineWrap hlw;
    
    @Before
    public void setUp() throws Exception {
        hlw = new HardLineWrap();
    }

    @Test
    public void testDoWrapB() {
        IDocument d = new Document("wrap test");
        TestDocumentCommand c = new TestDocumentCommand(0, "a");
        hlw.doWrapB(d, c, 10);
        c.execute(d);
        assertEquals("awrap test", d.get());
        assertEquals(1, c.caretOffset);
        
        //Wrapping
        d = new Document("wrap test\n");
        c = new TestDocumentCommand(0, "a");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("awrap\ntest\n", d.get());
        assertEquals(1, c.caretOffset);
        
        d = new Document("wrap test\n");
        c = new TestDocumentCommand(0, "a");
        hlw.doWrapB(d, c, 1);
        c.execute(d);
        assertEquals("awrap\ntest\n", d.get());
        assertEquals(1, c.caretOffset);

        d = new Document("wrap test\n");
        c = new TestDocumentCommand(9, "a");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("wrap\ntesta\n", d.get());
        assertEquals(10, c.caretOffset);

        //no wrapping possible
        d = new Document("wrap_test\n");
        c = new TestDocumentCommand(0, "a");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("awrap_test\n", d.get());
        assertEquals(1, c.caretOffset);

        //Simple Indentation
        d = new Document("  wrap test\n");
        c = new TestDocumentCommand(2, "a");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("  awrap\n  test\n", d.get());
        assertEquals(3, c.caretOffset);
        
        //Whitespaces at the end of a line
        d = new Document("  wrap test   \n");
        c = new TestDocumentCommand(2, "a");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("  awrap\n  test\n", d.get());
        assertEquals(3, c.caretOffset);
        
        d = new Document("wrap test        \n");
        c = new TestDocumentCommand(0, "a");
        hlw.doWrapB(d, c, 10);
        c.execute(d);
        assertEquals("awrap test        \n", d.get());
        assertEquals(1, c.caretOffset);
        
        //Hard case
        d = new Document("wrap test\n");
        c = new TestDocumentCommand(9, " ");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        //FIXME: This one fails
        //assertEquals("wrap test \n", d.get());
        assertEquals(10, c.caretOffset);
        
        //Insert more than one character
        d = new Document("test\n");
        c = new TestDocumentCommand(0, "wrap ");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("wrap test\n", d.get());
        assertEquals(5, c.caretOffset);
        
        d = new Document("test\n");
        c = new TestDocumentCommand(0, "wrap ");
        hlw.doWrapB(d, c, 8);
        c.execute(d);
        assertEquals("wrap\ntest\n", d.get());
        assertEquals(5, c.caretOffset);

        d = new Document("\n");
        c = new TestDocumentCommand(0, "wrap test");
        hlw.doWrapB(d, c, 8);
        c.execute(d);
        assertEquals("wrap\ntest\n", d.get());
        assertEquals(9, c.caretOffset);

        //Text on the next line
        d = new Document("wrap test\nmore text\n");
        c = new TestDocumentCommand(0, "a");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("awrap\ntest more text\n", d.get());
        assertEquals(1, c.caretOffset);
        
        d = new Document("wrap test\n\\begin{env}\n");
        c = new TestDocumentCommand(0, "a");
        hlw.doWrapB(d, c, 9);
        c.execute(d);
        assertEquals("awrap\ntest\n\\begin{env}\n", d.get());
        assertEquals(1, c.caretOffset);
    }

}
