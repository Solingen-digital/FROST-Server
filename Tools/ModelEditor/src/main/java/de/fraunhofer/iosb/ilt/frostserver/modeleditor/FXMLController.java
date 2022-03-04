/*
 * Copyright (C) 2017 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.frostserver.modeleditor;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditor;
import de.fraunhofer.iosb.ilt.configurable.ConfigEditors;
import de.fraunhofer.iosb.ilt.configurable.ConfigurationException;
import de.fraunhofer.iosb.ilt.frostserver.model.loader.DefModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

public class FXMLController implements Initializable {

    /**
     * The logger for this class.
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FXMLController.class);
    @FXML
    private ScrollPane paneConfig;
    @FXML
    private Button buttonLoad;
    @FXML
    private Button buttonSave;

    private ConfigEditor<?> configEditorModel;
    private final FileChooser fileChooser = new FileChooser();

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    @FXML
    private void actionLoad(ActionEvent event) throws ConfigurationException {
        loadModel();
    }

    private void loadModel() {
        JsonElement json = loadFromFile("Load Model");
        if (json == null) {
            return;
        }
        configEditorModel = ConfigEditors
                .buildEditorFromClass(DefModel.class, null, null)
                .get();
        configEditorModel.setConfig(json);
        replaceEditor();
    }

    private JsonElement loadFromFile(String title) {
        try {
            fileChooser.setTitle(title);
            File file = fileChooser.showOpenDialog(paneConfig.getScene().getWindow());
            if (file == null) {
                return null;
            }
            String config = FileUtils.readFileToString(file, "UTF-8");
            return JsonParser.parseString(config);
        } catch (IOException ex) {
            LOGGER.error("Failed to read file", ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("failed to read file");
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
        }
        return null;
    }

    @FXML
    private void actionSave(ActionEvent event) {
        saveModel();
    }

    private void saveModel() {
        JsonElement json = configEditorModel.getConfig();
        saveToFile(json, "Save Model");
    }

    private void saveToFile(JsonElement json, String title) {
        String config = new GsonBuilder().setPrettyPrinting().create().toJson(json);
        fileChooser.setTitle(title);
        File file = fileChooser.showSaveDialog(paneConfig.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            FileUtils.writeStringToFile(file, config, "UTF-8");
        } catch (IOException ex) {
            LOGGER.error("Failed to write file.", ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("failed to write file");
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    private void replaceEditor() {
        paneConfig.setContent(configEditorModel.getGuiFactoryFx().getNode());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configEditorModel = ConfigEditors
                .buildEditorFromClass(DefModel.class, null, null)
                .get();

        replaceEditor();
    }

    public void close() {
        LOGGER.info("Received close, shutting down executor.");
        List<Runnable> remaining = executor.shutdownNow();
        LOGGER.info("Remaining threads: {}", remaining.size());
    }
}
