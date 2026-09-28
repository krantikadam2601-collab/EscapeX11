package escapex;

import escapex.service.GameEngine;
import escapex.ui.EscapeXFrame;
import escapex.ui.OopConceptsDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class RenderSnapshot {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Snapshot Main Frame Content
                GameEngine engine = new GameEngine();
                EscapeXFrame frame = new EscapeXFrame(engine);
                frame.setSize(1200, 780);
                frame.addNotify();
                frame.validate();
                
                JComponent content = (JComponent) frame.getContentPane();
                content.setSize(1200, 780);
                layoutRecursively(content);

                BufferedImage frameImg = new BufferedImage(1200, 780, BufferedImage.TYPE_INT_RGB);
                Graphics2D gFrame = frameImg.createGraphics();
                content.printAll(gFrame);
                gFrame.dispose();
                ImageIO.write(frameImg, "png", new File("snapshot_frame.png"));
                System.out.println("Saved snapshot_frame.png");

                // 2. Snapshot OOP Guide Dialog Content (Topic 1: Encapsulation)
                OopConceptsDialog dialog = new OopConceptsDialog(frame);
                dialog.setSize(880, 640);
                dialog.addNotify();
                dialog.validate();

                JComponent dialogContent = (JComponent) dialog.getContentPane();
                dialogContent.setSize(880, 640);
                layoutRecursively(dialogContent);

                BufferedImage dialogImg = new BufferedImage(880, 640, BufferedImage.TYPE_INT_RGB);
                Graphics2D gDialog = dialogImg.createGraphics();
                dialogContent.printAll(gDialog);
                gDialog.dispose();
                ImageIO.write(dialogImg, "png", new File("snapshot_dialog.png"));
                System.out.println("Saved snapshot_dialog.png");

                // 3. Snapshot OOP Guide Dialog Content (Topic 2: Inheritance)
                dialog.selectTopic(1);
                layoutRecursively(dialogContent);
                BufferedImage dialogImg2 = new BufferedImage(880, 640, BufferedImage.TYPE_INT_RGB);
                Graphics2D gDialog2 = dialogImg2.createGraphics();
                dialogContent.printAll(gDialog2);
                gDialog2.dispose();
                ImageIO.write(dialogImg2, "png", new File("snapshot_dialog_topic2.png"));
                // 4. Snapshot Puzzle Dialog (Room 2 Hard Puzzle - Photonic Optical Lock)
                engine.solvePuzzle("p1_1", "1");
                engine.solvePuzzle("p1_2", "75");
                engine.exploreObject("obj_bench");
                engine.exploreObject("obj_breaker"); // 2nd item (card)
                engine.exploreObject("obj_shelf"); // 3rd item (manual)
                engine.solvePuzzle("p1_3", "4096");
                engine.moveToNextRoom(); // Move to Sector 02
                engine.exploreObject("obj_tape"); // Collect laser pointer
                escapex.ui.PuzzleDialog puzzleDialog = new escapex.ui.PuzzleDialog(frame, engine, escapex.model.puzzle.PuzzleDifficulty.HARD);
                puzzleDialog.setSize(680, 580);
                puzzleDialog.addNotify();
                puzzleDialog.validate();

                JComponent pzContent = (JComponent) puzzleDialog.getContentPane();
                pzContent.setSize(680, 580);
                layoutRecursively(pzContent);

                BufferedImage pzImg = new BufferedImage(680, 580, BufferedImage.TYPE_INT_RGB);
                Graphics2D gPz = pzImg.createGraphics();
                pzContent.printAll(gPz);
                gPz.dispose();
                ImageIO.write(pzImg, "png", new File("snapshot_puzzle.png"));
                System.out.println("Saved snapshot_puzzle.png");

                // 5. Progress to Sector 03 and collect all items up to 9 items
                engine.solvePuzzle("p2_1", "ESCAPE");
                engine.solvePuzzle("p2_2", "CHNYEEPRT");
                engine.solvePuzzle("p2_3", "MIRROR-45");
                engine.exploreObject("obj_pedestal"); // 5th item
                engine.exploreObject("obj_safe"); // 6th item
                engine.moveToNextRoom(); // Move to Sector 03 (The Neural Core)

                engine.exploreObject("obj_monitor"); // 7th item
                engine.exploreObject("obj_tensor_rack"); // 8th item (page 1 full)
                engine.exploreObject("obj_synapse"); // 9th item (weight_usb, on page 2!)

                frame.getInventoryPanel().refreshInventoryView();
                content.setSize(1200, 780);
                layoutRecursively(content);
                BufferedImage s3Img = new BufferedImage(1200, 780, BufferedImage.TYPE_INT_RGB);
                Graphics2D gS3 = s3Img.createGraphics();
                content.printAll(gS3);
                gS3.dispose();
                ImageIO.write(s3Img, "png", new File("snapshot_sector3_paginated.png"));
                System.out.println("Saved snapshot_sector3_paginated.png");

                // 6. Snapshot Neural Convergence Puzzle Dialog in Sector 03
                escapex.ui.PuzzleDialog neuralPzDialog = new escapex.ui.PuzzleDialog(frame, engine, escapex.model.puzzle.PuzzleDifficulty.HARD);
                neuralPzDialog.setSize(680, 580);
                neuralPzDialog.addNotify();
                neuralPzDialog.validate();

                JComponent nPzContent = (JComponent) neuralPzDialog.getContentPane();
                nPzContent.setSize(680, 580);
                layoutRecursively(nPzContent);

                BufferedImage nPzImg = new BufferedImage(680, 580, BufferedImage.TYPE_INT_RGB);
                Graphics2D gNPz = nPzImg.createGraphics();
                nPzContent.printAll(gNPz);
                gNPz.dispose();
                ImageIO.write(nPzImg, "png", new File("snapshot_neural_puzzle.png"));
                System.out.println("Saved snapshot_neural_puzzle.png");

                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void layoutRecursively(Container c) {
        c.doLayout();
        for (Component child : c.getComponents()) {
            if (child instanceof Container cont) {
                layoutRecursively(cont);
            }
        }
    }
}
