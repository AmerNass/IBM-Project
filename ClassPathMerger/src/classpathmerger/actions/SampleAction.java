package classpathmerger.actions;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		//		MessageDialog.openInformation(
		//			window.getShell(),
		//			"ClassPathMerger",
		//			"Hello, Eclipse world");

		//get object which represents the workspace  
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  

		//get location of workspace (java.io.File)  
		File workspaceDirectory = workspace.getRoot().getLocation().toFile();

		//System.out.println(workspaceDirectory.getAbsolutePath());

		List<File> files = findDirectoriesWithSameName(workspaceDirectory);

		String OS =System.getProperty("os.name").toLowerCase();

		if (isWindows(OS)) {
			for(File file : files)
			{
				//System.out.println(file.getAbsolutePath());
				String path = file.getAbsolutePath()+"\\.classpath";
				try {
					changeClassPath(path);
				} catch (ParserConfigurationException | SAXException
						| IOException | TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (isMac(OS) || isUnix(OS)) {
			for(File file : files)
			{
				//System.out.println(file.getAbsolutePath());
				String path = file.getAbsolutePath()+"/.classpath";
				try {
					changeClassPath(path);
				} catch (ParserConfigurationException | SAXException
						| IOException | TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			MessageDialog.openInformation(
					window.getShell(),
					"ClassPathMerger",
					"Your OS is not supported !");
		}




	}


	public void changeClassPath(String path) throws ParserConfigurationException, SAXException, IOException, TransformerException
	{

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(path);
        Element root = document.getDocumentElement();

        // Root Element
        Element rootElement = document.getDocumentElement();


        File file = new File("C:\\Users\\Nasser.STARWARS\\runtime-EclipseApplication\\commonEnv");
        
        for (File i : findJars(file)) {
            // server elements
            Element jar = document.createElement("classpathentry");
            jar.setAttribute("kind", "lib");
            jar.setAttribute("path", "C:\\Users\\Nasser.STARWARS\\runtime-EclipseApplication\\commonEnv\\"+i.getName());
            rootElement.appendChild(jar);
        }

        DOMSource source = new DOMSource(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(path);
        transformer.transform(source, result);
    }

    public static class Server {
        public String getName() { return "foo"; }
        public Integer getPort() { return 12345; }
    }



public static boolean isWindows(String OS) {

	return (OS.indexOf("win") >= 0);

}

public static boolean isMac(String OS) {

	return (OS.indexOf("mac") >= 0);

}

public static boolean isUnix(String OS) {

	return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );

}


public static List<File> findJars(File root) {
	List<File> result = new ArrayList<>();

	for (File file : root.listFiles()) {

			if(file.getName().endsWith(".jar"))
				result.add(file);
	}

	return result;
}


public static List<File> findDirectoriesWithSameName(File root) {
	List<File> result = new ArrayList<>();

	for (File file : root.listFiles()) {
		if (file.isDirectory()) {

			if(!file.getName().equals(".metadata") && !file.getName().equals("commonEnv"))
				result.add(file);

			//result.addAll(findDirectoriesWithSameName(name, file));
		}
	}

	return result;
}

/**
 * Selection in the workbench has been changed. We 
 * can change the state of the 'real' action here
 * if we want, but this can only happen after 
 * the delegate has been created.
 * @see IWorkbenchWindowActionDelegate#selectionChanged
 */
public void selectionChanged(IAction action, ISelection selection) {
}

/**
 * We can use this method to dispose of any system
 * resources we previously allocated.
 * @see IWorkbenchWindowActionDelegate#dispose
 */
public void dispose() {
}

/**
 * We will cache window object in order to
 * be able to provide parent shell for the message dialog.
 * @see IWorkbenchWindowActionDelegate#init
 */
public void init(IWorkbenchWindow window) {
	this.window = window;
}
}