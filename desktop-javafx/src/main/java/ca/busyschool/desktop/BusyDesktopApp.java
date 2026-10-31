package ca.busyschool.desktop;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public final class BusyDesktopApp extends Application {
    private final BusyApiClient api = new BusyApiClient();
    private BorderPane shell;
    private VBox navigation;
    private Label connectionLabel;
    private String currentPage = "Overview";
    private String apiBase = "http://localhost:8080";

    @Override public void start(Stage stage) {
        shell = new BorderPane();
        shell.setStyle("-fx-background-color: #f4f6fa;");
        shell.setLeft(buildSidebar());
        shell.setTop(buildTopbar());
        showPage("Overview");

        Scene scene = new Scene(shell, 1280, 800);
        stage.setTitle("BUSY | School Software Center");
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
        checkConnection();
    }

    private VBox buildSidebar() {
        navigation = new VBox(7);
        navigation.setPadding(new Insets(24, 14, 20, 14));
        navigation.setPrefWidth(238);
        navigation.setStyle("-fx-background-color: #111827;");
        Label brand = new Label("BUSY");
        brand.setTextFill(Color.WHITE);
        brand.setFont(Font.font("System", 27));
        Label subtitle = new Label("SCHOOL SOFTWARE CENTER");
        subtitle.setTextFill(Color.web("#9ca3af"));
        subtitle.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-letter-spacing: 1.2px;");
        VBox logo = new VBox(3, brand, subtitle);
        logo.setPadding(new Insets(2, 8, 28, 8));
        navigation.getChildren().add(logo);
        for (String item : List.of("Overview", "Software catalog", "Software requests", "Device inventory", "Reports", "Settings")) {
            Button button = new Button(item);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setAlignment(Pos.CENTER_LEFT);
            button.setPadding(new Insets(12, 13, 12, 13));
            button.setStyle(navStyle(item.equals(currentPage)));
            button.setOnAction(e -> showPage(item));
            navigation.getChildren().add(button);
        }
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label footer = new Label("NATIVE DESKTOP  •  v2.0");
        footer.setTextFill(Color.web("#6b7280"));
        footer.setStyle("-fx-font-size: 9px;");
        navigation.getChildren().addAll(spacer, footer);
        return navigation;
    }

    private String navStyle(boolean active) {
        return active
            ? "-fx-background-color: #29364b; -fx-text-fill: white; -fx-background-radius: 9; -fx-font-size: 13px;"
            : "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-background-radius: 9; -fx-font-size: 13px;";
    }

    private HBox buildTopbar() {
        Label section = new Label("BUSY  /  SCHOOL IT");
        section.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold;");
        connectionLabel = new Label("Checking server…");
        connectionLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bar = new HBox(12, section, spacer, connectionLabel);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(17, 26, 17, 26));
        bar.setStyle("-fx-background-color: white; -fx-border-color: #e5eaf1; -fx-border-width: 0 0 1 0;");
        return bar;
    }

    private void showPage(String page) {
        currentPage = page;
        if (navigation != null) {
            for (var node : navigation.getChildren()) {
                if (node instanceof Button b) b.setStyle(navStyle(b.getText().equals(page)));
            }
        }
        shell.setCenter(switch (page) {
            case "Software catalog" -> catalogPage();
            case "Software requests" -> simpleTablePage("Software requests", "Review and track software requests submitted by students and teachers.", new String[]{"Request", "Requested by", "Purpose", "Status"});
            case "Device inventory" -> simpleTablePage("Device inventory", "Track school computers, displays, printers and assigned equipment.", new String[]{"Asset tag", "Device", "Assigned to", "Location", "Status"});
            case "Reports" -> reportsPage();
            case "Settings" -> settingsPage();
            default -> overviewPage();
        });
    }

    private VBox pageFrame(String eyebrow, String title, String description) {
        VBox page = new VBox(18);
        page.setPadding(new Insets(30, 34, 34, 34));
        Label small = new Label(eyebrow.toUpperCase());
        small.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label heading = new Label(title);
        heading.setStyle("-fx-text-fill: #172033; -fx-font-size: 27px; -fx-font-weight: bold;");
        Label detail = new Label(description);
        detail.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        page.getChildren().addAll(small, heading, detail);
        return page;
    }

    private VBox overviewPage() {
        VBox page = pageFrame("Workspace", "Good day.", "Your school software and technology workspace.");
        HBox cards = new HBox(14,
            metricCard("PUBLISHED SOFTWARE", "—", "Connect the BUSY server to load"),
            metricCard("PENDING REQUESTS", "—", "Live counts from the server"),
            metricCard("PUBLISHED RELEASES", "—", "Windows and macOS packages"));
        page.getChildren().add(cards);
        VBox panel = panel();
        Label panelTitle = new Label("Welcome to BUSY");
        panelTitle.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #172033;");
        Label body = new Label("Use the navigation to browse approved software, review requests, manage school devices and view reports. Connect to your school's BUSY API in Settings to load live data.");
        body.setWrapText(true);
        body.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        panel.getChildren().addAll(panelTitle, body);
        page.getChildren().add(panel);
        return page;
    }

    private VBox metricCard(String label, String value, String note) {
        VBox card = panel();
        Label l = new Label(label); l.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label v = new Label(value); v.setStyle("-fx-text-fill: #172033; -fx-font-size: 29px; -fx-font-weight: bold;");
        Label n = new Label(note); n.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        card.getChildren().addAll(l, v, n);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setPrefWidth(230);
        return card;
    }

    private VBox panel() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e5eaf1; -fx-border-radius: 12;");
        return box;
    }

    private VBox catalogPage() {
        VBox page = pageFrame("Applications", "Software catalog", "Search published software available through your school.");
        TextField search = new TextField();
        search.setPromptText("Search by software name or publisher");
        search.setPrefWidth(420);
        Button refresh = new Button("Refresh catalog");
        refresh.setStyle(primaryButton());
        HBox actions = new HBox(10, search, refresh);
        actions.setAlignment(Pos.CENTER_LEFT);
        TableView<JsonNode> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        addColumn(table, "Software", "name", 190);
        addColumn(table, "Publisher", "publisher", 150);
        addColumn(table, "Category", "category", 130);
        addColumn(table, "Version", "version", 90);
        addColumn(table, "Platform", "platform", 100);
        Label status = new Label("Connect to the BUSY API to load published applications.");
        status.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        Runnable load = () -> {
            status.setText("Loading catalog…");
            api.catalog(search.getText()).whenComplete((rows, error) -> Platform.runLater(() -> {
                if (error != null) status.setText("Could not load catalog: " + rootMessage(error));
                else {
                    table.setItems(FXCollections.observableArrayList(rows));
                    status.setText(rows.size() + " published release(s) returned by the server.");
                }
            }));
        };
        refresh.setOnAction(e -> load.run());
        search.setOnAction(e -> load.run());
        page.getChildren().addAll(actions, table, status);
        VBox.setVgrow(table, Priority.ALWAYS);
        load.run();
        return page;
    }

    private void addColumn(TableView<JsonNode> table, String title, String key, double width) {
        TableColumn<JsonNode, String> col = new TableColumn<>(title);
        col.setPrefWidth(width);
        col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().path(key).asText("")));
        table.getColumns().add(col);
    }

    private VBox simpleTablePage(String title, String description, String[] columns) {
        VBox page = pageFrame("Management", title, description);
        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        for (int i = 0; i < columns.length; i++) {
            final int index = i;
            TableColumn<String[], String> col = new TableColumn<>(columns[i]);
            col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                index < data.getValue().length ? data.getValue()[index] : ""));
            table.getColumns().add(col);
        }
        Label note = new Label("This native screen is in place. Live records and editing actions will be enabled when the matching authenticated API endpoints are implemented.");
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        page.getChildren().addAll(panelWith(table), note);
        VBox.setVgrow(table, Priority.ALWAYS);
        return page;
    }

    private VBox panelWith(Control control) {
        VBox p = panel(); VBox.setVgrow(control, Priority.ALWAYS); p.getChildren().add(control); return p;
    }

    private VBox reportsPage() {
        VBox page = pageFrame("Insights", "Reports", "School software and device activity reporting.");
        VBox p = panel();
        Label title = new Label("Reporting service");
        title.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #172033;");
        Label info = new Label("The Python reporting service is part of the platform foundation. Configure its endpoint after the service is deployed to your school's environment.");
        info.setWrapText(true); info.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        p.getChildren().addAll(title, info);
        page.getChildren().add(p);
        return page;
    }

    private VBox settingsPage() {
        VBox page = pageFrame("Configuration", "Settings", "Connect this desktop app to your school's BUSY platform.");
        VBox p = panel();
        Label label = new Label("BUSY API address");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #172033;");
        TextField endpoint = new TextField(apiBase);
        endpoint.setPromptText("https://busy.school.example");
        Label hint = new Label("Use the HTTPS address supplied by your school IT administrator. Local development default: http://localhost:8080");
        hint.setWrapText(true); hint.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        Button save = new Button("Save and test connection");
        save.setStyle(primaryButton());
        Label result = new Label();
        save.setOnAction(e -> {
            String value = endpoint.getText().trim().replaceAll("/+$", "");
            if (!(value.startsWith("https://") || value.startsWith("http://localhost") || value.startsWith("http://127.0.0.1"))) {
                result.setText("Use HTTPS for remote servers. HTTP is allowed only for local development.");
                return;
            }
            apiBase = value;
            api.setBaseUrl(apiBase);
            result.setText("Testing connection…");
            api.health().whenComplete((ok, error) -> Platform.runLater(() -> {
                result.setText(error == null ? "Connected to BUSY API." : "Connection failed: " + rootMessage(error));
                connectionLabel.setText(error == null ? "● Server connected" : "● Server unavailable");
            }));
        });
        p.getChildren().addAll(label, endpoint, hint, save, result);
        page.getChildren().add(p);
        return page;
    }

    private String primaryButton() {
        return "-fx-background-color: #315efb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 15 10 15;";
    }

    private void checkConnection() {
        api.health().whenComplete((ok, error) -> Platform.runLater(() ->
            connectionLabel.setText(error == null ? "● Server connected" : "● Server not connected")));
    }

    private String rootMessage(Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }

    @Override public void stop() { api.close(); }

    public static void main(String[] args) { launch(args); }
}
