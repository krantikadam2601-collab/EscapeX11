package escapex;

import escapex.ui.OopConceptsDialog;
import javax.swing.SwingUtilities;

public class DialogTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                OopConceptsDialog dialog = new OopConceptsDialog(null);
                System.out.println("Dialog created successfully. Size: " + dialog.getSize());
                System.out.println("Component count: " + dialog.getContentPane().getComponentCount());
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
