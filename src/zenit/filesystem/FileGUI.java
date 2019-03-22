package zenit.filesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import zenit.filesystem.controller.FileController;

/**
 * Only used for testing the file-system features, not to be used in final product
 * @author Alexander Libot
 */
public class FileGUI {
	
	private FileController controller;
	
	private File openFile;
	private String selectedNode = null;
	
	private JPanel pnlMain;	
	private JTextPane tpText;
	private JLabel lblFilepath;
	private JTree trExplorer;
	private DefaultTreeModel treeModel;
	
	//Project buttons
	private JButton btnNewProject;
	private JButton btnOpenProject;
	private JButton btnSaveProject;
	private JButton btnRenameProject;
	private JButton btnDeleteProject;
	
	//Package buttons
	private JButton btnNewPackage;
	private JButton btnOpenPackage;
	private JButton btnSavePackage;
	private JButton btnRenamePackage;
	private JButton btnDeletePackage;
	
	//Class buttons
	private JButton btnNewClass;
	private JButton btnOpenClass;
	private JButton btnSaveClass;
	private JButton btnRenameClass;
	private JButton btnDeleteClass;	
	
	//Listeners
	private ProjectListener projectListener = new ProjectListener();
	private PackageListener packageListener = new PackageListener();
	private ClassListener classListener = new ClassListener();
	private TreeListener treeListener = new TreeListener();

	//Constructor
	public FileGUI() {
		putInFrame(initUI());
	}
	
	//Setters
	public void setController(FileController controller) {
		this.controller = controller;
	}
	
	public void setOpenFile(File file) {
		openFile = file;
		
		if (file != null) {
			lblFilepath.setText(openFile.getAbsolutePath());
		} else {
			lblFilepath.setText("Choose project");
		}
		pnlMain.updateUI();
	}
	
	//Getters
	public File getOpenFile() {
		return openFile;
	}
	
	//Public methods
	
	public void openFile(DefaultStyledDocument dsd) {
		tpText.setDocument(dsd);
		pnlMain.updateUI();
	}
	
	public void clearTextPane() {
		tpText.setDocument(new DefaultStyledDocument());
		pnlMain.updateUI();
	}
	
	public void openDialog(String message, String title, int optionType) {
		JOptionPane.showConfirmDialog(null, message, title, optionType);
	}
	
	//Tree manipulation
	public void initTree(File workspace) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Workspace");
		treeModel = new DefaultTreeModel(root);
		
		FileTree.createNodes(root, workspace);
		
		trExplorer = new JTree(treeModel);
		trExplorer.setRootVisible(false);
		
	    trExplorer.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    trExplorer.addTreeSelectionListener(treeListener);
		
	    JPanel pnlTree = new JPanel();
	    pnlTree.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    pnlTree.add(trExplorer);
		pnlMain.add(pnlTree, BorderLayout.WEST);
		pnlMain.updateUI();
	}
	
	public void addNode(String child) {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) trExplorer.getLastSelectedPathComponent();
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
		trExplorer.scrollPathToVisible(new TreePath(childNode.getPath()));
	}
	
	//FIXME I'm too stupid for trees
	public void addNode(String child, File file) {
		
		if (file.isDirectory()) {
			File[] entries = file.listFiles();
			for (File entry : entries) {
				addNode(entry.getName(), entry);
			}
		} else {
			
		}
	}
	
	public void renameNode(String nodeString) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) trExplorer.getLastSelectedPathComponent();
		node.setUserObject(nodeString);
		treeModel.reload(node);
		treeListener.valueChanged(null);
	}
	
	public void deleteNode() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) trExplorer.getLastSelectedPathComponent();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
		node.removeFromParent();
		treeModel.reload(parent);
	}
	
	//Private methods
	private JPanel initUI() {
		
		pnlMain = new JPanel(new BorderLayout());
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new GridLayout(3, 5));
		
		lblFilepath = new JLabel("Choose file to open");

		tpText = new JTextPane();
		tpText.setPreferredSize(new Dimension(600,300));
		tpText.setFont(new Font("monospaced", Font.PLAIN, 12));
		
		//Project buttons
		btnNewProject = new JButton("new project");
		btnOpenProject = new JButton("open project");
		btnSaveProject = new JButton("save project");
		btnRenameProject = new JButton("rename project");
		btnDeleteProject = new JButton("delete project");
		
		btnNewProject.addActionListener(projectListener);
		btnOpenProject.addActionListener(projectListener);
		btnSaveProject.addActionListener(projectListener);
		btnRenameProject.addActionListener(projectListener);
		btnDeleteProject.addActionListener(projectListener);
		
		pnlButtons.add(btnNewProject);
		pnlButtons.add(btnOpenProject);
		pnlButtons.add(btnSaveProject);
		pnlButtons.add(btnRenameProject);
		pnlButtons.add(btnDeleteProject);
		
		btnSaveProject.setEnabled(false);
		
		//Package buttons
		btnNewPackage = new JButton("new package");
		btnOpenPackage = new JButton("open package");
		btnSavePackage = new JButton("save package");
		btnRenamePackage = new JButton("rename package");
		btnDeletePackage = new JButton("delete package");
		
		btnNewPackage.addActionListener(packageListener);
		btnOpenPackage.addActionListener(packageListener);
		btnSavePackage.addActionListener(packageListener);
		btnRenamePackage.addActionListener(packageListener);
		btnDeletePackage.addActionListener(packageListener);
		
		pnlButtons.add(btnNewPackage);
		pnlButtons.add(btnOpenPackage);
		pnlButtons.add(btnSavePackage);
		pnlButtons.add(btnRenamePackage);
		pnlButtons.add(btnDeletePackage);
		
		btnSavePackage.setEnabled(false);
		
		//Class buttons
		btnNewClass = new JButton("new class");
		btnOpenClass = new JButton("open class");
		btnSaveClass = new JButton("save class");
		btnRenameClass = new JButton("rename class");
		btnDeleteClass = new JButton("delete class");
		
		btnNewClass.addActionListener(classListener);
		btnOpenClass.addActionListener(classListener);
		btnSaveClass.addActionListener(classListener);
		btnRenameClass.addActionListener(classListener);
		btnDeleteClass.addActionListener(classListener);
		
		pnlButtons.add(btnNewClass);
		pnlButtons.add(btnOpenClass);
		pnlButtons.add(btnSaveClass);
		pnlButtons.add(btnRenameClass);
		pnlButtons.add(btnDeleteClass);
		
		pnlMain.add(lblFilepath, BorderLayout.NORTH);
		pnlMain.add(tpText, BorderLayout.CENTER);
		pnlMain.add(pnlButtons, BorderLayout.SOUTH);
		
		return pnlMain;
	}
	
	private void putInFrame(JPanel panel) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	//Inner classes
	private class ProjectListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnNewProject)) {
				String chosenProjectName = JOptionPane.showInputDialog("Chose name for project");
				controller.createProject(chosenProjectName);
			} else if (e.getSource().equals(btnOpenProject)) {
				controller.openProject(selectedNode);
			} else if (e.getSource().equals(btnSaveProject)) {
				//TODO
			} else if (e.getSource().equals(btnRenameProject)) {
				String newProjectName = JOptionPane.showInputDialog("Enter new package name");
				controller.renameProject(selectedNode, newProjectName);
			} else if (e.getSource().equals(btnDeleteProject)) {
				controller.deleteProject(selectedNode);
			}
		}
	}
	
	private class PackageListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnNewPackage)) {
				String packageName = JOptionPane.showInputDialog("Enter package name");
				controller.createPackage(selectedNode, packageName);
			} else if (e.getSource().equals(btnOpenPackage)) {
				controller.openPackage(selectedNode);
			} else if (e.getSource().equals(btnSavePackage)) {
				//TODO
			} else if (e.getSource().equals(btnRenamePackage)) {
				String newPackagename = JOptionPane.showInputDialog("Enter new package name");
				controller.renamePackage(selectedNode, newPackagename);
			} else if (e.getSource().equals(btnDeletePackage)) {
				controller.deletePackage(selectedNode);
			}
		}
	}
	
	private class ClassListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(btnNewClass)) {
				if (selectedNode != null) {
					String chosenClassName = JOptionPane.showInputDialog("Chose name for class");
					controller.createFile(selectedNode, chosenClassName);
				}
			} else if (e.getSource().equals(btnOpenClass)) {
				if (selectedNode != null) {
					controller.readFile(selectedNode);
				}
			} else if (e.getSource().equals(btnSaveClass)) {
				controller.writeFile(openFile, (DefaultStyledDocument) tpText.getDocument());
			} else if (e.getSource().equals(btnRenameClass)) {
				String newFilename = JOptionPane.showInputDialog("Enter new class name");
				controller.renameFile(selectedNode, newFilename);
			} else if (e.getSource().equals(btnDeleteClass)) {
				controller.deleteFile(selectedNode);
			}
		}
	}
	
	private class TreeListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) trExplorer.getLastSelectedPathComponent();
			
			if (node != null) {
				TreeNode[] nodes = node.getPath();

				String path = "/";
				for (int index = 1; index < nodes.length; index++) {
					path += nodes[index] + "/";
				}

				selectedNode = path;
			}
		}
	}
}