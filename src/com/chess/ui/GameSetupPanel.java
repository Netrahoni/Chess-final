package com.chess.ui;

import com.chess.ChessGame;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class GameSetupPanel extends JPanel {
    private CustomToggleButton bulletButton;
    private CustomToggleButton blitzButton;
    private CustomToggleButton rapidButton;
    private final ChessGame chessGame;

    // 1. The constructor parameter is renamed to 'game'
    public GameSetupPanel(ChessGame game) {
        this.chessGame = game;
        // 2. The constructor now only calls the new private init method
        initComponents();
    }

    /**
     * Initializes all UI components for the setup panel.
     * This fixes the "overridable method call in constructor" warning.
     */
    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30));
        setPreferredSize(new Dimension(800, 800));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(new Color(118, 200, 84));
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("The ultimate challenge");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        gbc.insets = new Insets(0, 5, 40, 5);
        add(subtitleLabel, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);

        add(createSectionTitle("Game Mode"), gbc);
        JPanel modePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        modePanel.setOpaque(false);
        ButtonGroup modeGroup = new ButtonGroup();
        CustomToggleButton humanButton = new CustomToggleButton("👤 Human", true);
        CustomToggleButton aiButton = new CustomToggleButton("🤖 AI", false);
        modeGroup.add(humanButton);
        modeGroup.add(aiButton);
        modePanel.add(humanButton);
        modePanel.add(aiButton);
        gbc.insets = new Insets(5, 5, 20, 5);
        add(modePanel, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);


        JLabel aiLevelTitle = createSectionTitle("AI Level");
        JPanel aiLevelPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        aiLevelPanel.setOpaque(false);
        ButtonGroup aiLevelGroup = new ButtonGroup();
        CustomToggleButton easyButton = new CustomToggleButton("Easy", false);
        CustomToggleButton mediumButton = new CustomToggleButton("Medium", true);
        CustomToggleButton hardButton = new CustomToggleButton("Hard", false);
        aiLevelGroup.add(easyButton);
        aiLevelGroup.add(mediumButton);
        aiLevelGroup.add(hardButton);
        aiLevelPanel.add(easyButton);
        aiLevelPanel.add(mediumButton);
        aiLevelPanel.add(hardButton);

        aiLevelTitle.setVisible(false);
        aiLevelPanel.setVisible(false);
        add(aiLevelTitle, gbc);
        gbc.insets = new Insets(5, 5, 20, 5);
        add(aiLevelPanel, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);

        aiButton.addActionListener(e -> {
            aiLevelTitle.setVisible(true);
            aiLevelPanel.setVisible(true);
        });
        humanButton.addActionListener(e -> {
            aiLevelTitle.setVisible(false);
            aiLevelPanel.setVisible(false);
        });


        add(createSectionTitle("Game Type"), gbc);
        JPanel typePanel = new JPanel(new GridLayout(1, 3, 10, 0));
        typePanel.setOpaque(false);
        ButtonGroup typeGroup = new ButtonGroup();
        bulletButton = new CustomToggleButton("Bullet", false);
        blitzButton = new CustomToggleButton("Blitz", false);
        rapidButton = new CustomToggleButton("Rapid", true);
        typeGroup.add(bulletButton);
        typeGroup.add(blitzButton);
        typeGroup.add(rapidButton);
        typePanel.add(bulletButton);
        typePanel.add(blitzButton);
        typePanel.add(rapidButton);
        gbc.insets = new Insets(5, 5, 20, 5);
        add(typePanel, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);

        add(createSectionTitle("Time Control"), gbc);

        Map<String, String[]> timeOptionsMap = new HashMap<>();
        timeOptionsMap.put("Bullet", new String[]{"1 Minute"});
        timeOptionsMap.put("Blitz", new String[]{"3 Minutes", "5 Minutes"});
        timeOptionsMap.put("Rapid", new String[]{"10 Minutes", "15 Minutes", "30 Minutes", "Unlimited"});

        JComboBox<String> timeControlSelector = new JComboBox<>();
        timeControlSelector.setModel(new DefaultComboBoxModel<>(timeOptionsMap.get("Rapid")));
        timeControlSelector.setSelectedItem("15 Minutes");

        ActionListener gameTypeListener = e -> {
            String type = ((JToggleButton) e.getSource()).getText();
            timeControlSelector.setModel(new DefaultComboBoxModel<>(timeOptionsMap.get(type)));
        };

        bulletButton.addActionListener(gameTypeListener);
        blitzButton.addActionListener(gameTypeListener);
        rapidButton.addActionListener(gameTypeListener);

        timeControlSelector.setFont(new Font("Arial", Font.BOLD, 16));
        timeControlSelector.setBackground(new Color(60, 60, 60));
        timeControlSelector.setForeground(Color.WHITE);
        timeControlSelector.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(new Color(90, 90, 90));
                return button;
            }
        });
        timeControlSelector.setPreferredSize(new Dimension(300, 40));
        gbc.insets = new Insets(5, 5, 40, 5);
        add(timeControlSelector, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);


        JButton startGameButton = new JButton("▶ Start Game");
        startGameButton.setFont(new Font("Arial", Font.BOLD, 24));
        startGameButton.setBackground(new Color(118, 200, 84));
        startGameButton.setForeground(Color.WHITE);
        startGameButton.setFocusPainted(false);
        startGameButton.setBorder(new EmptyBorder(15, 40, 15, 40));
        startGameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 3. The lambda now correctly uses 'this.chessGame'
        startGameButton.addActionListener(e -> {
            if (aiButton.isSelected()) {
                JOptionPane.showMessageDialog(this.chessGame,
                        "This Feature will be Available soon...",
                        "Feature Not Available",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                String selectedTime = (String) timeControlSelector.getSelectedItem();
                String gameType = "";
                if (bulletButton.isSelected()) gameType = "Bullet";
                else if (blitzButton.isSelected()) gameType = "Blitz";
                else if (rapidButton.isSelected()) gameType = "Rapid";

                String mode = humanButton.isSelected() ? "Human" : "AI";
                String level = "N/A";
                if (aiButton.isSelected()) {
                    if (easyButton.isSelected()) level = "Easy";
                    else if (mediumButton.isSelected()) level = "Medium";
                    else level = "Hard";
                }

                if (selectedTime != null) {
                    this.chessGame.startGame(selectedTime, gameType, mode, level);
                }
            }
        });
        add(startGameButton, gbc);
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    private final class CustomToggleButton extends JToggleButton {
        private final Color normalColor = new Color(60, 60, 60);
        private final Color hoverColor = new Color(80, 80, 80);
        private final Color selectedColor = new Color(255, 200, 0);

        public CustomToggleButton(String text, boolean selected) {
            super(text);
            setSelected(selected);
            setFont(new Font("Arial", Font.BOLD, 18));
            setFocusPainted(false);
            setBorder(new EmptyBorder(10, 20, 10, 20));
            setContentAreaFilled(false);
            setOpaque(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isSelected()) {
                        setBackground(hoverColor);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    updateColor();
                }
            });
        }

        @Override
        public void setSelected(boolean b) {
            super.setSelected(b);
            updateColor();
        }

        @Override
        protected void paintComponent(Graphics g) {
            updateColor();
            super.paintComponent(g);
        }

        private void updateColor() {
            if (isSelected()) {
                setBackground(selectedColor);
                setForeground(Color.BLACK);
            } else {
                setBackground(normalColor);
                setForeground(Color.WHITE);
            }
        }
    }
}