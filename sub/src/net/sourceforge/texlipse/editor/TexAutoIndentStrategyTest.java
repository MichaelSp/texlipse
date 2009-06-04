package net.sourceforge.texlipse.editor;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.texlipse.TexlipsePlugin;
import net.sourceforge.texlipse.editor.HardLineWrapTest.TestDocumentCommand;
import net.sourceforge.texlipse.properties.TexlipseProperties;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TexAutoIndentStrategyTest {

    static TexAutoIndentStrategy autoIndent;
    
    @Before
    public void setUp() throws Exception {
    	IPreferenceStore fPreferenceStore = TexlipsePlugin.getDefault().getPreferenceStore();
    	fPreferenceStore.setValue(TexlipseProperties.TEX_ITEM_COMPLETION, true);
    	fPreferenceStore.setValue(TexlipseProperties.INDENTATION_LEVEL, 2);
    	fPreferenceStore.setValue(TexlipseProperties.INDENTATION_TABS, false);
    	fPreferenceStore.setValue(TexlipseProperties.INDENTATION, true);
    	fPreferenceStore.setValue(TexlipseProperties.INDENTATION_ENVS, "itemize,indent,description");
        autoIndent = new TexAutoIndentStrategy();
    }
    
    private String string;
    private int position;
    private String text;
    private String result;
    private int resultCursor;
    
    @Test
    public void testAutoIndent() {
        IDocument d = new Document(string);
        TestDocumentCommand c = new TestDocumentCommand(position, text);
        autoIndent.customizeDocumentCommand(d, c);

        c.execute(d);
        assertEquals(result, d.get());
        assertEquals(resultCursor, c.caretOffset);
    }
    
    @Parameters
    public static Collection testAutoIndentValues() {
    	return Arrays.asList(new Object[] [] {
    			//{Document text, insert position, ins. text, result, cursor position}
    			{"aa", 2, "\n", "aa\n", 3},
    			{"aa", 2, "\r\n", "aa\r\n", 4},
    			{"aa", 1, "\n", "a\na", 2},
    			{"  aa", 4, "\n", "  aa\n  ", 7},
    			{"  aa", 3, "\n", "  a\n  a", 6},
    			{"  aa     aa", 4, "\n", "  aa\n       aa", 7},
    			//Simple automatic closure of environments
    			{"\\begin{test}", 12, "\n", "\\begin{test}\n\n\\end{test}", 13},
    			{"\\begin{test}\n\\end{test}", 12, "\n", "\\begin{test}\n\n\\end{test}", 13},
    			{"\\begin{test}\\end{test}", 12, "\n", "\\begin{test}\n\\end{test}", 13},
    			{"  \\begin{test}", 14, "\n", "  \\begin{test}\n  \n  \\end{test}", 17},
    			//Simple automatic closure of environments (with Indentation)
    			{"\\begin{indent}", 14, "\n", "\\begin{indent}\n  \n\\end{indent}", 17},
    			{"\\begin{indent}\n\\end{indent}", 14, "\n", "\\begin{indent}\n  \n\\end{indent}", 17},
    			{"\\begin{indent}\\end{indent}", 14, "\n", "\\begin{indent}\n  \\end{indent}", 17},
    			{"  \\begin{indent}", 16, "\n", "  \\begin{indent}\n    \n  \\end{indent}", 21},
    			//Simple automatic \item insertion and closure of environments
    			{"\\begin{enumerate}", 17, "\n", "\\begin{enumerate}\n\\item \n\\end{enumerate}", 24},
    			{"\\begin{enumerate}\n\\item aa\n\\end{enumerate}", 26, "\n", "\\begin{enumerate}\n\\item aa\n\\item \n\\end{enumerate}", 33},
    			});
    }

    public TexAutoIndentStrategyTest(String string, int p, String text, String result, int resultCursor) {
    	this.string = string;
    	this.position = p;
    	this.text = text;
    	this.result = result;
    	this.resultCursor = resultCursor;
    }
}
