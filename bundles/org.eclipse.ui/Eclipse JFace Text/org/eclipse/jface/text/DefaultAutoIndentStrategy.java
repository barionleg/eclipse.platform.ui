package org.eclipse.jface.text;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */



/**
 * Default implementation of <code>IAutoIndentStrategy</code>.
 * This strategy always copies the indentation of the previous line.
 */
public class DefaultAutoIndentStrategy implements IAutoIndentStrategy {
	
	/**
	 * Creates a new default auto indent strategy which can be installed on
	 * text viewers.
	 */
	public DefaultAutoIndentStrategy() {
	}
	/**
	 * Copies the indentation of the previous line.
	 *
	 * @param d the document to work on
	 * @param c the command to deal with
	 */
	private void autoIndentAfterNewLine(IDocument d, DocumentCommand c) {
		
		if (c.offset == -1 || d.getLength() == 0)
			return;
			
		try {
			// find start of line
			int p= (c.offset == d.getLength() ? c.offset  - 1 : c.offset);
			IRegion info= d.getLineInformationOfOffset(p);
			int start= info.getOffset();
				
			// find white spaces
			int end= findEndOfWhiteSpace(d, start, c.offset);
				
			StringBuffer buf= new StringBuffer(c.text);
			if (end > start) {			
				// append to input
				buf.append(d.get(start, end - start));
			}
			
			c.text= buf.toString();
				
		} catch (BadLocationException excp) {
			// stop work
		}	
	}
	/*
	 * @see IAutoIndentStrategy#customizeDocumentCommand
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (c.length == 0 && c.text != null && TextUtilities.endsWith(d.getLegalLineDelimiters(), c.text) != -1)
			autoIndentAfterNewLine(d, c);
	}
	/**
	 * Returns the first offset greater than <code>offset</code> and smaller than 
	 * <code>end</code> whose character is not a space or tab character. If no such
	 * offset is found, <code>end</code> is returned.
	 *
	 * @param document the document to search in
	 * @param offset the offset at which searching start
	 * @param end the offset at which searching stops
	 * @return the offset in the specifed range whose character is not a space or tab
	 * @exception BadLocationException if position is an invalid range in the given document
	 */
	protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException {
		while (offset < end) {
			char c= document.getChar(offset);
			if (c != ' ' && c != '\t') {
				return offset;
			}
			offset++;
		}
		return end;
	}
}
