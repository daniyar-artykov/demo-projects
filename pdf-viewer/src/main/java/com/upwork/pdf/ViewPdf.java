package com.upwork.pdf;

import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdfviewer.PageWrapper;
import org.apache.pdfbox.pdfviewer.ReaderBottomPanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ExtensionFileFilter;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JLabel;


public class ViewPdf extends javax.swing.JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3183722463828632232L;


	private File currentDir=new File(".");


	private javax.swing.JMenu viewMenu;
	private javax.swing.JMenuItem nextPageItem;
	private javax.swing.JMenuItem previousPageItem;
	private JPanel documentPanel = new JPanel();

	private PDDocument document = null;
	private List pages= null;

	private int currentPage = 0;
	private int numberOfPages = 0;
	private String currentFilename = null;

	/**
	 * Constructor.
	 */
	public ViewPdf() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{

		viewMenu = new javax.swing.JMenu();
		nextPageItem = new javax.swing.JMenuItem();
		previousPageItem = new javax.swing.JMenuItem();


		setTitle("ViewPDF");
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				exitApplication();
			}
		});
		getContentPane().setLayout(null);


		JScrollPane documentScroller = new JScrollPane();
		documentScroller.setBounds(0, 46, 1050, 808);
		documentScroller.setColumnHeaderView( documentPanel );


		getContentPane().add( documentScroller );

		JLabel lblLabelHeader = new JLabel("Header");
		lblLabelHeader.setBounds(41, 11, 46, 14);
		getContentPane().add(lblLabelHeader);



		viewMenu.setText("View");
		nextPageItem.setText("Next page");
		nextPageItem.setAccelerator(KeyStroke.getKeyStroke('+'));
		nextPageItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				nextPage();
			}
		});
		viewMenu.add(nextPageItem);

		previousPageItem.setText("Previous page");
		previousPageItem.setAccelerator(KeyStroke.getKeyStroke('-'));
		previousPageItem.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				previousPage();
			}
		});
		viewMenu.add(previousPageItem);

		//        menuBar.add(viewMenu);
		//
		//        setJMenuBar(menuBar);


		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width-700)/2, (screenSize.height-600)/2, 1066, 914);
	}

	private void updateTitle() {
		setTitle( "PDFBox - " + currentFilename + " ("+(currentPage+1)+"/"+numberOfPages+")");
	}

	private void nextPage()
	{
		if (currentPage < numberOfPages-1) 
		{
			currentPage++;
			updateTitle();
			showPage(currentPage);
		}
	}

	private void previousPage()
	{
		if (currentPage > 0 ) 
		{
			currentPage--;
			updateTitle();
			showPage(currentPage);
		}
	}

	private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt)
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(currentDir);

		ExtensionFileFilter pdfFilter = new ExtensionFileFilter(new String[] {"PDF"}, "PDF Files");
		chooser.setFileFilter(pdfFilter);
		int result = chooser.showOpenDialog(ViewPdf.this);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			String name = chooser.getSelectedFile().getPath();
			currentDir = new File(name).getParentFile();
			try
			{
				openPDFFile(name);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void exitApplication()
	{
		try
		{
			if( document != null )
			{
				document.close();
			}
		}
		catch( IOException io )
		{

		}
		this.setVisible( false );
		this.dispose();
	}

	/**
	 * @param args the command line arguments
	 *
	 * @throws Exception If anything goes wrong.
	 */
	public static void main(String[] args) throws Exception
	{
		ViewPdf viewer = new ViewPdf();
		if( args.length >0 )
		{
			viewer.openPDFFile( args[0] );
		}
		viewer.setVisible(true);
	}

	private void openPDFFile(String file) throws Exception
	{
		if( document != null )
		{
			document.close();
			documentPanel.removeAll();
		}
		InputStream input = null;
		File f = new File( file );
		input = new FileInputStream(f);
		document = parseDocument( input );//        document = parseDocument( input );
		pages = document.getDocumentCatalog().getAllPages();
		numberOfPages = pages.size();
		currentFilename = f.getAbsolutePath();
		currentPage = 0;
		updateTitle();
		showPage(0);
	}

	private PDDocument parseDocument(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private void showPage(int pageNumber)
	{
		try 
		{
			PageWrapper wrapper = new PageWrapper(this);
			wrapper.displayPage( (PDPage)pages.get(pageNumber) );
			if (documentPanel.getComponentCount() > 0)
			{
				documentPanel.remove(0);
			}
			documentPanel.add( wrapper.getPanel() );
			pack();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
	/**
	 * This will parse a document.
	 *
	 * @param input The input stream for the document.
	 *
	 * @return The document.
	 *
	 * @throws IOException If there is an error parsing the document.
	 */
	private static PDDocument parseDocument( InputStream input )throws IOException, InvalidPasswordException
	{
		PDDocument document = PDDocument.load( input );
		if( document.isEncrypted() )
		{
			try
			{
				document.decrypt( "" );
			}
			catch( org.apache.pdfbox.exceptions.CryptographyException e )
			{
				e.printStackTrace();
			}
		}

		return document;
	}

	/**
	 * Get the bottom status panel.
	 *
	 * @return The bottom status panel.
	 */
	public ReaderBottomPanel getBottomStatusPanel()
	{
		return getBottomStatusPanel();
	}
}