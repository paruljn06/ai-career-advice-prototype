import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

// Main class to run the AI Career Advisor with GUI
public class AICareerAdvisorGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CareerAdvisorGUI().setVisible(true);
        });
    }
}

// Main GUI frame
class CareerAdvisorGUI extends JFrame {
    private User currentUser;
    private CareerAdvisor advisor;
    private JTabbedPane tabbedPane;

    // Components for different tabs
    private JPanel profilePanel;
    private JPanel recommendationsPanel;
    private JPanel learningPathPanel;
    private JPanel marketTrendsPanel;

    // Profile components
    private JList<String> skillsList;
    private DefaultListModel<String> skillsListModel;
    private JTextField newSkillField;
    private JSlider skillLevelSlider;
    private JTextField interestsField;
    private JComboBox<String> experienceComboBox;
    private JLabel skillLevelLabel;

    // Recommendations components
    private JTable careerTable;
    private DefaultTableModel careerTableModel;
    private JTextArea careerDetailsArea;

    // Learning path components
    private JComboBox<String> careerSelector;
    private JTextArea learningPathArea;

    // Store recommendations for later use
    private List<CareerPath> currentRecommendations;

    public CareerAdvisorGUI() {
        advisor = new CareerAdvisor();
        currentRecommendations = new ArrayList<>();
        setupFrame();
        createTabs();
        loadSampleData();
    }

    private void setupFrame() {
        setTitle("AI Career Advisor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();
        add(tabbedPane);
    }

    private void createTabs() {
        createProfileTab();
        createRecommendationsTab();
        createLearningPathTab();
        createMarketTrendsTab();
    }

    private void createProfileTab() {
        profilePanel = new JPanel(new BorderLayout(10, 10));
        profilePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Your Skills Profile", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        profilePanel.add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Skills list
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(new JLabel("Your Skills:"), gbc);

        skillsListModel = new DefaultListModel<>();
        skillsList = new JList<>(skillsListModel);
        JScrollPane skillsScrollPane = new JScrollPane(skillsList);
        skillsScrollPane.setPreferredSize(new Dimension(300, 150));

        gbc.gridy = 1;
        gbc.gridheight = 3;
        contentPanel.add(skillsScrollPane, gbc);

        // Add skill panel
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        contentPanel.add(new JLabel("Add New Skill:"), gbc);

        gbc.gridy = 2;
        newSkillField = new JTextField(15);
        contentPanel.add(newSkillField, gbc);

        gbc.gridy = 3;
        JButton addSkillButton = new JButton("Add Skill");
        addSkillButton.addActionListener(e -> addSkill());
        contentPanel.add(addSkillButton, gbc);

        // Skill level
        gbc.gridx = 3;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Skill Level:"), gbc);

        gbc.gridy = 2;
        JPanel sliderPanel = new JPanel(new BorderLayout());
        skillLevelSlider = new JSlider(1, 5, 3);
        skillLevelSlider.setMajorTickSpacing(1);
        skillLevelSlider.setPaintTicks(true);
        skillLevelSlider.setPaintLabels(true);
        skillLevelLabel = new JLabel("Level: 3", JLabel.CENTER);

        skillLevelSlider.addChangeListener(e -> {
            skillLevelLabel.setText("Level: " + skillLevelSlider.getValue());
        });

        sliderPanel.add(skillLevelSlider, BorderLayout.CENTER);
        sliderPanel.add(skillLevelLabel, BorderLayout.SOUTH);
        contentPanel.add(sliderPanel, gbc);

        // Remove skill button
        gbc.gridy = 3;
        JButton removeSkillButton = new JButton("Remove Selected");
        removeSkillButton.addActionListener(e -> removeSkill());
        contentPanel.add(removeSkillButton, gbc);

        // Interests
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(new JLabel("Your Interests (comma separated):"), gbc);

        gbc.gridy = 5;
        interestsField = new JTextField();
        contentPanel.add(interestsField, gbc);

        // Experience level
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(new JLabel("Experience Level:"), gbc);

        gbc.gridy = 5;
        experienceComboBox = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Expert"});
        contentPanel.add(experienceComboBox, gbc);

        // Save button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        JButton saveProfileButton = new JButton("Save Profile");
        saveProfileButton.addActionListener(e -> saveProfile());
        contentPanel.add(saveProfileButton, gbc);

        profilePanel.add(contentPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Profile", profilePanel);
    }

    private void createRecommendationsTab() {
        recommendationsPanel = new JPanel(new BorderLayout(10, 10));
        recommendationsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Career Recommendations", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        recommendationsPanel.add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Career", "Match %", "Growth Potential"};
        careerTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        careerTable = new JTable(careerTableModel);
        careerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        careerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = careerTable.getSelectedRow();
                if (selectedRow != -1 && selectedRow < currentRecommendations.size()) {
                    showCareerDetails(currentRecommendations.get(selectedRow));
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(careerTable);
        recommendationsPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Career Details"));
        careerDetailsArea = new JTextArea(5, 50);
        careerDetailsArea.setEditable(false);
        careerDetailsArea.setLineWrap(true);
        careerDetailsArea.setWrapStyleWord(true);
        detailsPanel.add(new JScrollPane(careerDetailsArea), BorderLayout.CENTER);

        recommendationsPanel.add(detailsPanel, BorderLayout.SOUTH);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Get Recommendations");
        refreshButton.addActionListener(e -> refreshRecommendations());
        buttonPanel.add(refreshButton);
        recommendationsPanel.add(buttonPanel, BorderLayout.NORTH);

        tabbedPane.addTab("Recommendations", recommendationsPanel);
    }

    private void createLearningPathTab() {
        learningPathPanel = new JPanel(new BorderLayout(10, 10));
        learningPathPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Learning Path Generator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        learningPathPanel.add(titleLabel, BorderLayout.NORTH);

        // Career selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Career Path:"));

        careerSelector = new JComboBox<>();
        selectionPanel.add(careerSelector);

        JButton generateButton = new JButton("Generate Learning Path");
        generateButton.addActionListener(e -> generateLearningPath());
        selectionPanel.add(generateButton);

        JButton refreshButton = new JButton("Refresh Careers");
        refreshButton.addActionListener(e -> refreshCareerSelector());
        selectionPanel.add(refreshButton);

        learningPathPanel.add(selectionPanel, BorderLayout.NORTH);

        // Learning path display
        learningPathArea = new JTextArea();
        learningPathArea.setEditable(false);
        learningPathArea.setLineWrap(true);
        learningPathArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(learningPathArea);

        learningPathPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Learning Path", learningPathPanel);
    }

    private void createMarketTrendsTab() {
        marketTrendsPanel = new JPanel(new BorderLayout(10, 10));
        marketTrendsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Market Trends Analysis", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        marketTrendsPanel.add(titleLabel, BorderLayout.NORTH);

        // Trends text area
        JTextArea trendsArea = new JTextArea();
        trendsArea.setEditable(false);
        trendsArea.setLineWrap(true);
        trendsArea.setWrapStyleWord(true);

        // Sample market trends data
        StringBuilder trendsText = new StringBuilder();
        trendsText.append("=== CURRENT MARKET TRENDS ===\n\n");
        trendsText.append("In-Demand Skills:\n");
        trendsText.append("• Artificial Intelligence/Machine Learning\n");
        trendsText.append("• Cloud Computing (AWS, Azure, GCP)\n");
        trendsText.append("• Cybersecurity\n");
        trendsText.append("• Data Science and Analytics\n");
        trendsText.append("• DevOps and CI/CD Pipelines\n");
        trendsText.append("• Full Stack Development\n\n");

        trendsText.append("Emerging Fields:\n");
        trendsText.append("• Quantum Computing\n");
        trendsText.append("• Augmented Reality/Virtual Reality\n");
        trendsText.append("• Edge Computing\n");
        trendsText.append("• Internet of Things (IoT)\n");
        trendsText.append("• Blockchain Technology\n\n");

        trendsText.append("Job Market Outlook:\n");
        trendsText.append("The tech job market continues to grow with high demand for specialized skills. ");
        trendsText.append("Remote work opportunities have increased by 40% since 2020. ");
        trendsText.append("Salaries for AI and cybersecurity roles have seen the highest growth at 15-20% year-over-year.");

        trendsArea.setText(trendsText.toString());

        JScrollPane scrollPane = new JScrollPane(trendsArea);
        marketTrendsPanel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Market Data");
        refreshButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Market data refreshed with latest trends!", "Info", JOptionPane.INFORMATION_MESSAGE));
        marketTrendsPanel.add(refreshButton, BorderLayout.SOUTH);

        tabbedPane.addTab("Market Trends", marketTrendsPanel);
    }

    private void loadSampleData() {
        // Create a sample user
        currentUser = new User("user1", "John Doe", "john@example.com");
        currentUser.setSkills(Arrays.asList("Java", "Python", "SQL"));

        Map<String, Integer> skillLevels = new HashMap<>();
        skillLevels.put("Java", 4);
        skillLevels.put("Python", 3);
        skillLevels.put("SQL", 4);
        currentUser.setSkillLevels(skillLevels);

        currentUser.setInterests(Arrays.asList("AI", "Web Development", "Data Science"));
        currentUser.setExperienceLevel("Intermediate");

        // Update UI with user data
        updateSkillsList();

        interestsField.setText(String.join(", ", currentUser.getInterests()));
        experienceComboBox.setSelectedItem(currentUser.getExperienceLevel());

        // Load sample career recommendations
        refreshRecommendations();
    }

    private void updateSkillsList() {
        skillsListModel.clear();
        for (String skill : currentUser.getSkills()) {
            skillsListModel.addElement(skill + " (Level: " + currentUser.getSkillLevels().get(skill) + ")");
        }
    }

    private void addSkill() {
        String newSkill = newSkillField.getText().trim();
        if (!newSkill.isEmpty()) {
            int level = skillLevelSlider.getValue();

            // Add to user's skills
            List<String> skills = new ArrayList<>(currentUser.getSkills());
            skills.add(newSkill);
            currentUser.setSkills(skills);

            // Add to user's skill levels
            Map<String, Integer> skillLevels = new HashMap<>(currentUser.getSkillLevels());
            skillLevels.put(newSkill, level);
            currentUser.setSkillLevels(skillLevels);

            // Update UI
            updateSkillsList();
            newSkillField.setText("");

            JOptionPane.showMessageDialog(this, "Skill added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a skill name", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSkill() {
        int selectedIndex = skillsList.getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedSkill = skillsListModel.getElementAt(selectedIndex);
            String skillName = selectedSkill.split(" ")[0]; // Extract just the skill name

            // Remove from user's skills
            List<String> skills = new ArrayList<>(currentUser.getSkills());
            skills.remove(skillName);
            currentUser.setSkills(skills);

            // Remove from user's skill levels
            Map<String, Integer> skillLevels = new HashMap<>(currentUser.getSkillLevels());
            skillLevels.remove(skillName);
            currentUser.setSkillLevels(skillLevels);

            // Update UI
            updateSkillsList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a skill to remove", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProfile() {
        // Update interests
        String interestsText = interestsField.getText();
        List<String> interests = Arrays.asList(interestsText.split("\\s*,\\s*"));
        currentUser.setInterests(interests);

        // Update experience level
        currentUser.setExperienceLevel((String) experienceComboBox.getSelectedItem());

        JOptionPane.showMessageDialog(this, "Profile saved successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshRecommendations() {
        // Clear existing data
        careerTableModel.setRowCount(0);

        // Get recommendations
        currentRecommendations = advisor.getCareerRecommender().recommendCareers(currentUser);

        // Populate table
        for (CareerPath career : currentRecommendations) {
            careerTableModel.addRow(new Object[]{
                    career.getName(),
                    String.format("%.1f%%", career.getMatchScore()),
                    career.getGrowthPotential()
            });
        }

        // Update career selector in learning path tab
        refreshCareerSelector();
    }

    private void refreshCareerSelector() {
        careerSelector.removeAllItems();
        for (CareerPath career : currentRecommendations) {
            careerSelector.addItem(career.getName());
        }
    }

    private void showCareerDetails(CareerPath career) {
        // Create details text
        StringBuilder details = new StringBuilder();
        details.append("Career: ").append(career.getName()).append("\n");
        details.append("Match Score: ").append(String.format("%.1f%%", career.getMatchScore())).append("\n");
        details.append("Description: ").append(career.getDescription()).append("\n");
        details.append("Growth Potential: ").append(career.getGrowthPotential()).append("\n");
        details.append("Required Skills: ").append(String.join(", ", career.getRequiredSkills())).append("\n");
        details.append("Related Roles: ").append(String.join(", ", career.getRelatedRoles()));

        careerDetailsArea.setText(details.toString());
    }

    private void generateLearningPath() {
        String selectedCareerName = (String) careerSelector.getSelectedItem();
        if (selectedCareerName != null) {
            // Find the career path object
            CareerPath selectedCareer = null;
            for (CareerPath career : currentRecommendations) {
                if (career.getName().equals(selectedCareerName)) {
                    selectedCareer = career;
                    break;
                }
            }

            if (selectedCareer != null) {
                // Generate learning path
                LearningPath learningPath = advisor.getLearningPathGenerator().generateLearningPath(currentUser, selectedCareer);

                // Format the learning path text
                StringBuilder pathText = new StringBuilder();
                pathText.append("Learning Path for: ").append(selectedCareer.getName()).append("\n\n");
                pathText.append("Estimated Duration: ").append(learningPath.getEstimatedDuration()).append(" weeks\n\n");
                pathText.append("Skills to Develop:\n");
                for (String skill : learningPath.getSkillsToDevelop()) {
                    pathText.append("• ").append(skill).append("\n");
                }

                pathText.append("\nRecommended Resources:\n");
                for (LearningResource resource : learningPath.getResources()) {
                    pathText.append("• ").append(resource.getName()).append(" (").append(resource.getType()).append(")\n");
                    pathText.append("  Time Required: ").append(resource.getEstimatedHours()).append(" hours\n");
                    pathText.append("  URL: ").append(resource.getUrl()).append("\n\n");
                }

                learningPathArea.setText(pathText.toString());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a career path first", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

// Core Career Advisor class
class CareerAdvisor {
    private UserProfileManager profileManager;
    private SkillMapper skillMapper;
    private CareerRecommender careerRecommender;
    private LearningPathGenerator learningPathGenerator;
    private MarketAnalyzer marketAnalyzer;

    public CareerAdvisor() {
        this.profileManager = new UserProfileManager();
        this.skillMapper = new SkillMapper();
        this.careerRecommender = new CareerRecommender();
        this.learningPathGenerator = new LearningPathGenerator();
        this.marketAnalyzer = new MarketAnalyzer();
    }

    public CareerRecommender getCareerRecommender() {
        return careerRecommender;
    }

    public LearningPathGenerator getLearningPathGenerator() {
        return learningPathGenerator;
    }
}

// User class
class User {
    private String id;
    private String name;
    private String email;
    private List<String> skills;
    private Map<String, Integer> skillLevels;
    private List<String> interests;
    private String experienceLevel;
    private LocalDate lastUpdated;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.skills = new ArrayList<>();
        this.skillLevels = new HashMap<>();
        this.interests = new ArrayList<>();
        this.experienceLevel = "Beginner";
        this.lastUpdated = LocalDate.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) {
        this.skills = skills;
        this.lastUpdated = LocalDate.now();
    }

    public Map<String, Integer> getSkillLevels() { return skillLevels; }
    public void setSkillLevels(Map<String, Integer> skillLevels) {
        this.skillLevels = skillLevels;
        this.lastUpdated = LocalDate.now();
    }

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) {
        this.interests = interests;
        this.lastUpdated = LocalDate.now();
    }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
        this.lastUpdated = LocalDate.now();
    }

    public LocalDate getLastUpdated() { return lastUpdated; }
}

// User Profile Manager class
class UserProfileManager {
    private Map<String, User> users;

    public UserProfileManager() {
        this.users = new HashMap<>();
    }

    public User getUserProfile(String userId) {
        return users.get(userId);
    }

    public void saveUserProfile(User user) {
        users.put(user.getId(), user);
    }
}

// Skill Mapper class
class SkillMapper {
    private Map<String, List<String>> skillRelations;

    public SkillMapper() {
        // Initialize skill relationships
        skillRelations = new HashMap<>();
        skillRelations.put("Java", Arrays.asList("Object-Oriented Programming", "Spring Framework", "JUnit"));
        skillRelations.put("Python", Arrays.asList("Data Analysis", "Machine Learning", "Django"));
        skillRelations.put("JavaScript", Arrays.asList("React", "Node.js", "TypeScript"));
        skillRelations.put("SQL", Arrays.asList("Database Design", "Query Optimization", "NoSQL"));
        skillRelations.put("Machine Learning", Arrays.asList("Python", "Statistics", "Data Analysis"));
        skillRelations.put("Data Analysis", Arrays.asList("Python", "SQL", "Statistics"));
        skillRelations.put("React", Arrays.asList("JavaScript", "Frontend Development", "UI/UX"));
        skillRelations.put("Node.js", Arrays.asList("JavaScript", "Backend Development", "API Design"));
        skillRelations.put("Docker", Arrays.asList("Containers", "DevOps", "Cloud Computing"));
        skillRelations.put("AWS", Arrays.asList("Cloud Computing", "DevOps", "Infrastructure"));
    }

    public List<String> getRelatedSkills(String skill) {
        return skillRelations.getOrDefault(skill, new ArrayList<>());
    }

    public Map<String, List<String>> getSkillGaps(User user, CareerPath career) {
        Map<String, List<String>> gaps = new HashMap<>();
        List<String> userSkills = user.getSkills();

        for (String requiredSkill : career.getRequiredSkills()) {
            if (!userSkills.contains(requiredSkill)) {
                // Find related skills the user might have
                List<String> relatedSkills = new ArrayList<>();
                for (String userSkill : userSkills) {
                    if (getRelatedSkills(userSkill).contains(requiredSkill)) {
                        relatedSkills.add(userSkill);
                    }
                }
                gaps.put(requiredSkill, relatedSkills);
            }
        }

        return gaps;
    }
}

// Career Path class
class CareerPath {
    private String id;
    private String name;
    private String description;
    private List<String> requiredSkills;
    private double matchScore;
    private String growthPotential;
    private List<String> relatedRoles;

    public CareerPath(String id, String name, String description, List<String> requiredSkills,
                      String growthPotential, List<String> relatedRoles) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.growthPotential = growthPotential;
        this.relatedRoles = relatedRoles;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
    public String getGrowthPotential() { return growthPotential; }
    public List<String> getRelatedRoles() { return relatedRoles; }
}

// Career Recommender class
class CareerRecommender {
    private List<CareerPath> careerDatabase;

    public CareerRecommender() {
        // Initialize with sample career paths
        careerDatabase = new ArrayList<>();
        careerDatabase.add(new CareerPath(
                "cp1", "Data Scientist",
                "Analyze and interpret complex data to help organizations make better decisions",
                Arrays.asList("Python", "Statistics", "Machine Learning", "Data Visualization", "SQL"),
                "High",
                Arrays.asList("Data Analyst", "Machine Learning Engineer", "Business Analyst")
        ));

        careerDatabase.add(new CareerPath(
                "cp2", "Full Stack Developer",
                "Develop both client-side and server-side components of web applications",
                Arrays.asList("JavaScript", "HTML/CSS", "Node.js", "React", "Database Management", "API Design"),
                "High",
                Arrays.asList("Frontend Developer", "Backend Developer", "Web Developer")
        ));

        careerDatabase.add(new CareerPath(
                "cp3", "DevOps Engineer",
                "Bridge the gap between development and operations teams to improve collaboration",
                Arrays.asList("Linux", "Docker", "Kubernetes", "CI/CD", "Cloud Computing", "Scripting"),
                "High",
                Arrays.asList("Site Reliability Engineer", "Cloud Architect", "Systems Administrator")
        ));

        careerDatabase.add(new CareerPath(
                "cp4", "AI Engineer",
                "Design and implement artificial intelligence solutions for various applications",
                Arrays.asList("Python", "Machine Learning", "Deep Learning", "TensorFlow", "Data Analysis", "Mathematics"),
                "Very High",
                Arrays.asList("Machine Learning Engineer", "Data Scientist", "Research Scientist")
        ));

        careerDatabase.add(new CareerPath(
                "cp5", "Cybersecurity Analyst",
                "Protect organizations from cyber threats and ensure data security",
                Arrays.asList("Network Security", "Ethical Hacking", "Risk Assessment", "Cryptography", "Linux"),
                "High",
                Arrays.asList("Security Consultant", "Penetration Tester", "Security Architect")
        ));

        careerDatabase.add(new CareerPath(
                "cp6", "Cloud Solutions Architect",
                "Design and implement cloud-based solutions for businesses",
                Arrays.asList("Cloud Computing", "AWS", "Azure", "System Design", "Networking", "Security"),
                "Very High",
                Arrays.asList("Cloud Engineer", "Infrastructure Architect", "DevOps Engineer")
        ));
    }

    public List<CareerPath> recommendCareers(User user) {
        List<CareerPath> recommendations = new ArrayList<>();
        SkillMapper skillMapper = new SkillMapper();

        for (CareerPath career : careerDatabase) {
            double matchScore = calculateMatchScore(user, career, skillMapper);

            if (matchScore > 20) { // Recommend if at least 20% match
                CareerPath careerCopy = new CareerPath(
                        career.getId(),
                        career.getName(),
                        career.getDescription(),
                        new ArrayList<>(career.getRequiredSkills()),
                        career.getGrowthPotential(),
                        new ArrayList<>(career.getRelatedRoles())
                );
                careerCopy.setMatchScore(matchScore);
                recommendations.add(careerCopy);
            }
        }

        // Sort by match score descending
        recommendations.sort((c1, c2) -> Double.compare(c2.getMatchScore(), c1.getMatchScore()));

        return recommendations;
    }

    private double calculateMatchScore(User user, CareerPath career, SkillMapper skillMapper) {
        List<String> userSkills = user.getSkills();
        List<String> requiredSkills = career.getRequiredSkills();

        if (requiredSkills.isEmpty()) return 0;

        double matchedSkills = 0;
        for (String skill : requiredSkills) {
            if (userSkills.contains(skill)) {
                // Exact match
                matchedSkills += 1.0;
            } else {
                // Check for related skills
                for (String userSkill : userSkills) {
                    if (skillMapper.getRelatedSkills(userSkill).contains(skill)) {
                        // Partial match for related skills
                        matchedSkills += 0.3;
                        break;
                    }
                }
            }
        }

        // Consider experience level
        double experienceMultiplier = 1.0;
        if ("Intermediate".equals(user.getExperienceLevel())) {
            experienceMultiplier = 1.2;
        } else if ("Expert".equals(user.getExperienceLevel())) {
            experienceMultiplier = 1.5;
        }

        return (matchedSkills / requiredSkills.size()) * 100 * experienceMultiplier;
    }
}

// Learning Path class
class LearningPath {
    private String id;
    private CareerPath targetCareer;
    private List<LearningResource> resources;
    private List<String> skillsToDevelop;
    private int estimatedDuration;

    public LearningPath(String id, CareerPath targetCareer, List<LearningResource> resources,
                        List<String> skillsToDevelop, int estimatedDuration) {
        this.id = id;
        this.targetCareer = targetCareer;
        this.resources = resources;
        this.skillsToDevelop = skillsToDevelop;
        this.estimatedDuration = estimatedDuration;
    }

    // Getters
    public String getId() { return id; }
    public CareerPath getTargetCareer() { return targetCareer; }
    public List<LearningResource> getResources() { return resources; }
    public List<String> getSkillsToDevelop() { return skillsToDevelop; }
    public int getEstimatedDuration() { return estimatedDuration; }
}

// Learning Resource class
class LearningResource {
    private String id;
    private String name;
    private String type;
    private String url;
    private int estimatedHours;
    private List<String> skillsCovered;

    public LearningResource(String id, String name, String type, String url,
                            int estimatedHours, List<String> skillsCovered) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.estimatedHours = estimatedHours;
        this.skillsCovered = skillsCovered;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getUrl() { return url; }
    public int getEstimatedHours() { return estimatedHours; }
    public List<String> getSkillsCovered() { return skillsCovered; }
}

// Learning Path Generator class
class LearningPathGenerator {
    private Map<String, List<LearningResource>> learningResources;

    public LearningPathGenerator() {
        // Initialize with sample learning resources
        learningResources = new HashMap<>();

        // Data Science resources
        learningResources.put("Python", Arrays.asList(
                new LearningResource("lr1", "Python for Data Science", "Course",
                        "https://www.coursera.org/specializations/python", 40,
                        Arrays.asList("Python", "Data Analysis")),
                new LearningResource("lr2", "Python Crash Course", "Book",
                        "https://nostarch.com/pythoncrashcourse", 25,
                        Arrays.asList("Python", "Programming Fundamentals"))
        ));

        learningResources.put("Machine Learning", Arrays.asList(
                new LearningResource("lr3", "Intro to Machine Learning", "Course",
                        "https://www.coursera.org/learn/machine-learning", 60,
                        Arrays.asList("Machine Learning", "Python")),
                new LearningResource("lr4", "Hands-On ML with Scikit-Learn", "Book",
                        "https://www.oreilly.com/library/view/hands-on-machine-learning/9781492032632/", 35,
                        Arrays.asList("Machine Learning", "Python", "Scikit-Learn"))
        ));

        // Web Development resources
        learningResources.put("JavaScript", Arrays.asList(
                new LearningResource("lr5", "Modern JavaScript From The Beginning", "Course",
                        "https://www.udemy.com/course/modern-javascript/", 35,
                        Arrays.asList("JavaScript", "ES6")),
                new LearningResource("lr6", "JavaScript: The Good Parts", "Book",
                        "https://www.oreilly.com/library/view/javascript-the-good/9780596517748/", 15,
                        Arrays.asList("JavaScript", "Best Practices"))
        ));

        learningResources.put("React", Arrays.asList(
                new LearningResource("lr7", "React - The Complete Guide", "Course",
                        "https://www.udemy.com/course/react-the-complete-guide-incl-redux/", 45,
                        Arrays.asList("React", "JavaScript")),
                new LearningResource("lr8", "Fullstack React", "Book",
                        "https://www.fullstackreact.com/", 30,
                        Arrays.asList("React", "Node.js", "MongoDB"))
        ));

        // Other skills
        learningResources.put("Docker", Arrays.asList(
                new LearningResource("lr9", "Docker Mastery", "Course",
                        "https://www.udemy.com/course/docker-mastery/", 20,
                        Arrays.asList("Docker", "Containers")),
                new LearningResource("lr10", "Docker Deep Dive", "Book",
                        "https://www.amazon.com/Docker-Deep-Dive-Nigel-Poulton/dp/1521822808", 15,
                        Arrays.asList("Docker", "DevOps"))
        ));

        learningResources.put("Cloud Computing", Arrays.asList(
                new LearningResource("lr11", "AWS Certified Solutions Architect", "Course",
                        "https://www.udemy.com/course/aws-certified-solutions-architect-associate/", 30,
                        Arrays.asList("AWS", "Cloud Computing")),
                new LearningResource("lr12", "Cloud Architecture Patterns", "Book",
                        "https://www.oreilly.com/library/view/cloud-architecture-patterns/9781449357979/", 20,
                        Arrays.asList("Cloud Computing", "Architecture"))
        ));

        learningResources.put("Data Analysis", Arrays.asList(
                new LearningResource("lr13", "Data Analysis with Python", "Course",
                        "https://www.coursera.org/learn/data-analysis-with-python", 25,
                        Arrays.asList("Data Analysis", "Python", "Pandas")),
                new LearningResource("lr14", "Python for Data Analysis", "Book",
                        "https://www.oreilly.com/library/view/python-for-data/9781491957653/", 20,
                        Arrays.asList("Data Analysis", "Python", "NumPy"))
        ));

        learningResources.put("Node.js", Arrays.asList(
                new LearningResource("lr15", "The Complete Node.js Developer Course", "Course",
                        "https://www.udemy.com/course/the-complete-nodejs-developer-course-2/", 30,
                        Arrays.asList("Node.js", "JavaScript", "Backend")),
                new LearningResource("lr16", "Node.js Design Patterns", "Book",
                        "https://www.packtpub.com/product/node-js-design-patterns-third-edition/9781839214110", 25,
                        Arrays.asList("Node.js", "Design Patterns", "JavaScript"))
        ));
    }

    public LearningPath generateLearningPath(User user, CareerPath career) {
        SkillMapper skillMapper = new SkillMapper();
        Map<String, List<String>> skillGaps = skillMapper.getSkillGaps(user, career);

        List<LearningResource> resources = new ArrayList<>();
        List<String> skillsToDevelop = new ArrayList<>(skillGaps.keySet());
        int totalHours = 0;

        for (String skill : skillGaps.keySet()) {
            if (learningResources.containsKey(skill)) {
                // Add the first resource for each missing skill
                LearningResource resource = learningResources.get(skill).get(0);
                resources.add(resource);
                totalHours += resource.getEstimatedHours();
            }
        }

        // Estimate duration based on 10 hours per week
        int estimatedWeeks = (int) Math.ceil(totalHours / 10.0);

        return new LearningPath(
                "lp_" + user.getId() + "_" + career.getId(),
                career,
                resources,
                skillsToDevelop,
                estimatedWeeks
        );
    }
}

// Market Analyzer class
class MarketAnalyzer {
    public Map<String, String> getCurrentTrends() {
        // In a real application, this would fetch data from APIs or databases
        Map<String, String> trends = new HashMap<>();

        trends.put("inDemandSkills",
                "AI/Machine Learning, Cloud Computing, Cybersecurity, Data Science, " +
                        "Full Stack Development, DevOps, Blockchain, IoT");

        trends.put("emergingFields",
                "Quantum Computing, Augmented Reality/Virtual Reality, " +
                        "Edge Computing, Bioinformatics, Sustainable Technology");

        trends.put("marketOutlook",
                "The tech job market continues to grow with high demand for specialized skills. " +
                        "Remote work opportunities have increased by 40% since 2020. " +
                        "Salaries for AI and cybersecurity roles have seen the highest growth at 15-20% year-over-year.");

        return trends;
    }

    public List<String> getGrowingFields() {
        return Arrays.asList(
                "Artificial Intelligence", "Cybersecurity", "Cloud Computing",
                "Data Science", "DevOps", "Blockchain", "Internet of Things"
        );
    }
}