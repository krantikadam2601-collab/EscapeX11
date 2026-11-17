package escapex;

import escapex.model.GameDifficulty;
import escapex.service.GameEngine;
import escapex.ui.EscapeXFrame;
import escapex.ui.GameOverDialog;
import escapex.ui.HowToPlayDialog;
import escapex.ui.OopConceptsDialog;
import escapex.ui.PauseDialog;
import escapex.ui.SectorTransitionDialog;
import escapex.ui.VictoryDialog;

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
                // 1. Snapshot Main Frame: Futuristic Welcome Screen (Main Menu)
                GameEngine engine = new GameEngine();
                EscapeXFrame frame = new EscapeXFrame(engine);
                frame.setSize(1240, 800);
                frame.addNotify();
                frame.validate();

                JComponent menuContent = (JComponent) frame.getContentPane();
                menuContent.setSize(1240, 800);
                layoutRecursively(menuContent);

                BufferedImage menuImg = new BufferedImage(1240, 800, BufferedImage.TYPE_INT_RGB);
                Graphics2D gMenu = menuImg.createGraphics();
                menuContent.printAll(gMenu);
                gMenu.dispose();
                ImageIO.write(menuImg, "png", new File("snapshot_frame.png"));
                System.out.println("Saved snapshot_frame.png (Main Menu)");

                // 2. Start game session & snapshot active game view
                engine.startNewGame("Agent Cipher", GameDifficulty.NORMAL);
                frame.showGameView();
                layoutRecursively(menuContent);

                BufferedImage gameImg = new BufferedImage(1240, 800, BufferedImage.TYPE_INT_RGB);
                Graphics2D gGame = gameImg.createGraphics();
                menuContent.printAll(gGame);
                gGame.dispose();
                ImageIO.write(gameImg, "png", new File("snapshot_game_view.png"));
                System.out.println("Saved snapshot_game_view.png");

                // 3. Snapshot How to Play Field Manual
                HowToPlayDialog howToPlay = new HowToPlayDialog(frame);
                howToPlay.setSize(720, 600);
                howToPlay.addNotify();
                howToPlay.validate();
                JComponent htpContent = (JComponent) howToPlay.getContentPane();
                htpContent.setSize(720, 600);
                layoutRecursively(htpContent);

                BufferedImage htpImg = new BufferedImage(720, 600, BufferedImage.TYPE_INT_RGB);
                Graphics2D gHtp = htpImg.createGraphics();
                htpContent.printAll(gHtp);
                gHtp.dispose();
                ImageIO.write(htpImg, "png", new File("snapshot_how_to_play.png"));
                System.out.println("Saved snapshot_how_to_play.png");

                // 4. Snapshot Pause Dialog
                PauseDialog pauseDialog = new PauseDialog(frame, engine, null);
                pauseDialog.setSize(460, 480);
                pauseDialog.addNotify();
                pauseDialog.validate();
                JComponent pauseContent = (JComponent) pauseDialog.getContentPane();
                pauseContent.setSize(460, 480);
                layoutRecursively(pauseContent);

                BufferedImage pauseImg = new BufferedImage(460, 480, BufferedImage.TYPE_INT_RGB);
                Graphics2D gPause = pauseImg.createGraphics();
                pauseContent.printAll(gPause);
                gPause.dispose();
                ImageIO.write(pauseImg, "png", new File("snapshot_pause_menu.png"));
                System.out.println("Saved snapshot_pause_menu.png");

                // 5. Snapshot Sector Transition Dialog
                SectorTransitionDialog transitionDialog = new SectorTransitionDialog(frame, engine.getAllRooms().get(1), 1);
                transitionDialog.setSize(560, 480);
                transitionDialog.addNotify();
                transitionDialog.validate();
                JComponent transContent = (JComponent) transitionDialog.getContentPane();
                transContent.setSize(560, 480);
                layoutRecursively(transContent);

                BufferedImage transImg = new BufferedImage(560, 480, BufferedImage.TYPE_INT_RGB);
                Graphics2D gTrans = transImg.createGraphics();
                transContent.printAll(gTrans);
                gTrans.dispose();
                ImageIO.write(transImg, "png", new File("snapshot_sector_transition.png"));
                System.out.println("Saved snapshot_sector_transition.png");

                // 6. Snapshot Victory Dialog
                engine.getPlayer().addScore(5000);
                VictoryDialog victoryDialog = new VictoryDialog(frame, engine, null);
                victoryDialog.setSize(540, 560);
                victoryDialog.addNotify();
                victoryDialog.validate();
                JComponent vicContent = (JComponent) victoryDialog.getContentPane();
                vicContent.setSize(540, 560);
                layoutRecursively(vicContent);

                BufferedImage vicImg = new BufferedImage(540, 560, BufferedImage.TYPE_INT_RGB);
                Graphics2D gVic = vicImg.createGraphics();
                vicContent.printAll(gVic);
                gVic.dispose();
                ImageIO.write(vicImg, "png", new File("snapshot_victory.png"));
                System.out.println("Saved snapshot_victory.png");

                // 7. Snapshot Game Over Dialog
                GameOverDialog gameOverDialog = new GameOverDialog(frame, engine, null);
                gameOverDialog.setSize(540, 560);
                gameOverDialog.addNotify();
                gameOverDialog.validate();
                JComponent goContent = (JComponent) gameOverDialog.getContentPane();
                goContent.setSize(540, 560);
                layoutRecursively(goContent);

                BufferedImage goImg = new BufferedImage(540, 560, BufferedImage.TYPE_INT_RGB);
                Graphics2D gGo = goImg.createGraphics();
                goContent.printAll(gGo);
                gGo.dispose();
                ImageIO.write(goImg, "png", new File("snapshot_game_over.png"));
                System.out.println("Saved snapshot_game_over.png");

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
