package wellness.ui;

import wellness.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginDialog extends JDialog {
    private static final String DEMO_USER_ID = "demo-user";
    private final AuthService authService;
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private String authenticatedUserId;

    public LoginDialog(AuthService authService) {
        super((Frame) null, "Elite Wellness Login", true);
        this.authService = authService;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(240, 240, 240));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Header with logo
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel logo = new JLabel("⬤");
        logo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        logo.setForeground(Color.BLACK);
        JLabel title = new JLabel("Elite Wellness");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        title.setForeground(Color.BLACK);
        JLabel subtitle = new JLabel("Worldwide Health Intelligence");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
        subtitle.setForeground(new Color(80, 80, 80));
        header.add(logo, BorderLayout.WEST);
        JPanel textArea = new JPanel(new GridLayout(2, 1));
        textArea.setOpaque(false);
        textArea.add(title);
        textArea.add(subtitle);
        header.add(textArea, BorderLayout.CENTER);

        // Form
        JPanel form = new JPanel(new GridLayout(4, 1, 0, 15));
        form.setOpaque(false);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tfEmail = new JTextField();
        tfEmail.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        tfEmail.setPreferredSize(new Dimension(0, 40));
        tfEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        pfPassword = new JPasswordField();
        pfPassword.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        pfPassword.setPreferredSize(new Dimension(0, 40));
        pfPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            new EmptyBorder(8, 8, 8, 8)
        ));

        form.add(emailLabel);
        form.add(tfEmail);
        form.add(passwordLabel);
        form.add(pfPassword);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 3, 10, 0));
        buttons.setOpaque(false);

        JButton btnLogin = createStyledButton("Sign In", new Color(0, 0, 0), Color.WHITE);
        JButton btnRegister = createStyledButton("Create Account", new Color(240, 240, 240), Color.BLACK);
        JButton btnDemo = createStyledButton("Continue Demo", new Color(255, 255, 255), Color.BLACK);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> doRegister());
        btnDemo.addActionListener(e -> {
            authenticatedUserId = DEMO_USER_ID;
            dispose();
        });

        buttons.add(btnRegister);
        buttons.add(btnDemo);
        buttons.add(btnLogin);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(form, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(bgColor);
                RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2d.fill(rect);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(rect);
                super.paintComponent(g);
            }
        };
        btn.setForeground(fgColor);
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(0, 45));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private void doLogin() {
        try {
            String email = tfEmail.getText().trim();
            String password = new String(pfPassword.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and password required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            var acct = authService.authenticate(email, password);
            if (acct == null) {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            authenticatedUserId = acct.getUserId();
            dispose();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doRegister() {
        try {
            String email = tfEmail.getText().trim();
            String password = new String(pfPassword.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and password required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            var acct = authService.createUser(email, password);
            JOptionPane.showMessageDialog(this, "Account created: " + acct.getUserId(), "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Show the dialog and return the authenticated user id, or null if canceled.
     */
    public String showDialog() {
        setVisible(true);
        return authenticatedUserId;
    }
}
