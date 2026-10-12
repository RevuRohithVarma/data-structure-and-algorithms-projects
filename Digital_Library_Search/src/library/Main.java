package library;
import library.loader.ResourceLoader;import library.model.DigitalResource;import library.ui.Dashboard;import java.io.File;import java.util.ArrayList;import javax.swing.SwingUtilities;
public class Main {public static void main(String[]args)throws Exception{File root=new File("data");if(args.length>0)root=new File(args[0]);ArrayList<DigitalResource>d=ResourceLoader.load(root);System.out.println("Loaded "+d.size()+" resources.");DigitalResource[] a=d.toArray(new DigitalResource[0]); SwingUtilities.invokeLater(()->new Dashboard(a).setVisible(true));}}
