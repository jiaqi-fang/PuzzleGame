import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.*;

/**
 * The JavaPuzzle class is used to implement puzzle games.
 * @author jqfang
 */
public class JavaPuzzle {
	private JFileChooser jfc;
	private JFrame jf, jfm, jfw;
	private JPanel jp, jpb;
	private JButton[] jbi, jb;
	private JButton jbm, jbw;
	private JTextArea jta;
	private JLabel jlm, jlw;
	private int[] rand;
	private int cnt;
	
	private final int isize = 600;
	private final int inum = 5;
	private final int ilen = isize / inum;

	public static void main(String[] args) {
		// TODO Auto-generated method stub	
		JavaPuzzle myPuzzle = new JavaPuzzle();
		myPuzzle.init();
	}
	
	/**
	 * initialize the puzzle game
	 */
	public void init() {
		initFrame();
		File myFile = chooseFile();
		if(myFile == null)
			System.exit(-1);
		
		if(initImage(myFile)) {
			initWidget();
			show();
		}
		else
			printError();
	}
	
	/**
	 * initialize the game frame
	 */
	public void initFrame() {
		jf = new JFrame("Puzzle Image");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);
		jf.setLayout(null);
	}
	
	/**
	 * implement a file chooser and let the user choose an image file
	 * @return the image file for the puzzle game
	 */
	public File chooseFile() {
		jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.showDialog(new JLabel(), "Choose");
		File file = jfc.getSelectedFile();
		return file;
	}
	
	/**
	 * randomize the image and implement it to the game
	 * @param file the image file for the puzzle game
	 * @return whether implementation is successful
	 */
	public boolean initImage(File file) {
		try {
			BufferedImage fimg = ImageIO.read(file);
			BufferedImage img = new BufferedImage(
					isize, isize, BufferedImage.TYPE_INT_RGB);
			img.getGraphics().drawImage(fimg, 0, 0, isize, isize, null);
			BufferedImage[] subimgs = new BufferedImage[inum * inum];
			ImageIcon[] ic = new ImageIcon[inum * inum];
			jbi = new JButton[inum * inum];
			rand = randomize(inum * inum);
			cnt = 0;
			for(int i = 0; i < inum * inum; ++i)
				if(rand[i] == i)
					++cnt;
			
			for(int i = 0; i < inum; ++i)
				for(int j = 0; j < inum; ++j) {
					int index = i * inum + j;
					subimgs[index] = img.getSubimage(
							j * ilen, i * ilen, ilen, ilen);
					ic[index] = new ImageIcon(subimgs[index]);
					jbi[index] = new JButton(ic[index]);
					jbi[index].addMouseListener(new BlockListener());
				}
			
			jp = new JPanel();
			jp.setLayout(null);
			for(int i = 0; i < inum; ++i) 
				for(int j = 0; j < inum; ++j) {
					int index = i * inum + j;
					jp.add(jbi[rand[index]]);
					jbi[rand[index]].setBounds(j * ilen, i * ilen, ilen, ilen);
				}
			jf.getContentPane().add(jp);
			jp.setBounds(0, 0, isize, isize);
			return true;
		} catch(IOException e) {
			return false;
		}
	}
	
	/**
	 * initialize other widgets (a text area and buttons) for the puzzle game
	 */
	public void initWidget() {
		jta = new JTextArea();
		jta.setLineWrap(true);
		jta.setText("Game started!\n");
		JScrollPane jsp = new JScrollPane(jta);
		jsp.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jf.getContentPane().add(jsp);
		jsp.setBounds(10, isize + 10, isize - 20, 50);
		
		jpb = new JPanel();
		jpb.setLayout(new FlowLayout());
		jb = new JButton[3];
		jb[0] = new JButton("Load Another Image");
		jb[1] = new JButton("Show Original Image");
		jb[2] = new JButton("Exit");
		for(int i = 0; i < 3; ++i) {
			jpb.add(jb[i]);
			jb[i].addActionListener(new ButtonListener());
		}
		
		jf.getContentPane().add(jpb);
		jpb.setBounds(10, isize + 60, isize - 20, 40);
	}
	
	/**
	 * display the game frame
	 */
	public void show() {
		jf.setSize(isize, isize + 120);
		jf.setVisible(true);
	}
	
	/**
	 * print error messages for image loading failure
	 */
	public void printError() {
		jfm = new JFrame("Message");
		jfm.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		jfm.setLayout(null);
		jlm = new JLabel("Failed to load!");
		jbm = new JButton("Try another image");
		jbm.addActionListener(new ButtonListener());
		jfm.getContentPane().add(jlm);
		jlm.setBounds(50, 20, 100 ,30);
		jfm.getContentPane().add(jbm);
		jbm.setBounds(25, 60, 150, 30);
		jfm.setResizable(false);
		jfm.setSize(200, 120);
		jfm.setVisible(true);
	}

	/**
	 * display the original image
	 */
	public void showOriginal() {
		jp.setVisible(false);
		jp.removeAll();
		for(int i = 0; i < inum; ++i) 
			for(int j = 0; j < inum; ++j) {
				int index = i * inum + j;
				jp.add(jbi[index]);
				jbi[index].setBounds(j * ilen, i * ilen, ilen, ilen);
			}
		jp.setVisible(true);
		jta.append("Original image displayed!\n");
	}
	
	/**
	 * load another image and initialize another game
	 */
	public void reset() {
		jf.removeAll();
		jf.setVisible(false);
		init();
	}
	
	/**
	 * display winning messages
	 */
	public void win() {
		jfw = new JFrame("Message");
		jfw.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jfw.setLayout(null);
		jlw = new JLabel("You win!!!");
		jbw = new JButton("OK");
		jbw.addActionListener(new ButtonListener());
		jfw.getContentPane().add(jlw);
		jlw.setBounds(65, 20, 70 ,30);
		jfw.getContentPane().add(jbw);
		jbw.setBounds(70, 60, 60, 30);
		jfw.setResizable(false);
		jfw.setSize(200, 120);
		jfw.setVisible(true);
	}
	
	private int[] randomize(int n) {
		int ra[] = new int[n];
		for(int i = 0; i < n; ++i)
			ra[i] = i;
		Random r = new Random();
		for(int i = 0; i < n; ++i) {
			int j = r.nextInt(n);
			int temp = ra[j];
			ra[j] = ra[i];
			ra[i] = temp;
		}
		return ra;
	}
	
	private class ButtonListener implements ActionListener {
		/**
		 * reaction to action performed
		 * @param e event
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if(jbm != null && e.getSource() == jbm) {
				jfm.setVisible(false);
				init();
				jfm.dispose();
			}
			if(jb[0] != null && e.getSource() == jb[0])
				reset();
			if(jb[1] != null &&e.getSource() == jb[1])
				showOriginal();
			if(jb[2] != null &&e.getSource() == jb[2])
				System.exit(0);
			if(jbw != null &&e.getSource() == jbw) {
				jfw.setVisible(false);
				jfw.dispose();
			}
		}	
	}
	
	private class BlockListener implements MouseListener {
		int srcx, srcy, dstx, dsty;
		
		/**
		 * reaction to mouse clicked event
		 * @param e event
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		/**
		 * reaction to mouse pressed event
		 * @param e event
		 * @see java.awt.event.MouseListener#mousePressd(java.awt.event.MouseEvent)
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			int ox = jbi[rand[0]].getLocationOnScreen().x;
			int oy = jbi[rand[0]].getLocationOnScreen().y;
			srcx = (int) ((e.getXOnScreen() - ox) / ilen);
			srcy = (int) ((e.getYOnScreen() - oy) / ilen);		
		}

		/**
		 * reaction to mouse released event
		 * @param e event
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			int ox = jbi[rand[0]].getLocationOnScreen().x;
			int oy = jbi[rand[0]].getLocationOnScreen().y;
			dstx = (int) ((e.getXOnScreen() - ox) / ilen);
			dsty = (int) ((e.getYOnScreen() - oy) / ilen);
			
			int src = srcy * inum + srcx;
			int dst = dsty * inum + dstx;
			if(srcx >= 0 && srcx < inum && srcy >= 0 && srcy < inum
					&& dstx >= 0 && dstx < inum && dsty >= 0 && dsty < inum
					&& src != dst) {
				if(rand[src] == src)
					jta.append("Image block has been in correct position!\n");
				else if(rand[dst] == dst)
					jta.append("Image block has been in correct position!\n");
				else {			
					jp.setVisible(false);
					jbi[rand[src]].setBounds(dstx * ilen, dsty * ilen, ilen, ilen);	
					jbi[rand[dst]].setBounds(srcx * ilen, srcy * ilen, ilen, ilen);	
					int temp = rand[dst];
					rand[dst] = rand[src];
					rand[src] = temp;
					if(rand[dst] == dst) {
						++cnt;
						jta.append("Image block in correct position!\n");
					}
					if(rand[src] == src) {
						++cnt;
						jta.append("Image block in correct position!\n");
					}
					jp.setVisible(true);
				}
			}
			
			if(cnt == inum * inum)
				win();
		}

		/**
		 * reaction to mouse entered event
		 * @param e event
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		/**
		 * reaction to mouse exited event
		 * @param e event
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			
		}
		
	}

}
