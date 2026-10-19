package ni.edu.uam.demologin.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ni.edu.uam.demologin.Main;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.ArchivoService;
import ni.edu.uam.demologin.service.NavegationService;
import ni.edu.uam.demologin.service.UsuarioService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class RegistroController {
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmaPassword;
    @FXML
    private ImageView imageFoto;

    private String rutaFoto;

    private final ArchivoService
             archivoService = new ArchivoService();
    private final NavegationService
            navegationService = new NavegationService();
    private final UsuarioService
            usuarioService = new UsuarioService();

    @FXML
    private void seleccionarFoto(ActionEvent event)
    {
        File archivo = seleccionarArchivo();
        if (archivo == null) {
            return;
        }
        try {
            rutaFoto = archivoService.guardarArchivo(
                    archivo
            );
            mostrarImagen(rutaFoto);
        }
        catch (Exception e) {
            UtilController.mostrarException(e);
        }
    }

    private void mostrarImagen(String rutaFoto) {
        Image image = new Image(
                new File(rutaFoto).toURI().toString()
        );
        imageFoto.setImage(image);
    }

    private File seleccionarArchivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo");
        fc.getExtensionFilters()
                .add(new FileChooser
                        .ExtensionFilter("Imagenes"
                        , "*.jpg"
                        , "*.png", "*.jpeg"));
        return fc.showOpenDialog(
                imageFoto
                        .getScene()
                        .getWindow());
    }

    @FXML
    private void registrar(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }
        //Orden del constructor: nombre, apellido, correo, password, usuario, rutafoto
        Usuario usuario = new Usuario(
                txtNombre.getText().trim(),
                txtApellido.getText().trim(),
                txtCorreo.getText().trim(),
                txtPassword.getText(),
                txtUsuario.getText().trim(),
                rutaFoto
        );
        try {
            usuarioService.save(usuario);
        } catch (Exception e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo registrar el usuario: " + e.getMessage()
            );
            return;
        }
        UtilController.mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Éxito",
                "Usuario registrado correctamente"
        );
        mostrarUsuario(usuario);
        regresar(event);
    }

    private void mostrarUsuario(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("usuario-modal.fxml")
            );
            Parent root = loader.load();
            UsuarioModalController controller =
                    loader.getController();
            controller.setUsuario(
                    usuario
            );
            Stage stage = new Stage();
            stage.setTitle("Usuario");
            stage.setScene(
                    new Scene(root)
            );
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            UtilController.mostrarException(e);
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().isBlank()
            || txtApellido.getText().isBlank()
            || txtCorreo.getText().isBlank()
            || txtUsuario.getText().isBlank()
            || txtPassword.getText().isBlank()
            || txtConfirmaPassword.getText().isBlank()
        ) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Validacion",
                    "Todos los campos son obligatorios"
            );
            return false;
        }
        if (!txtPassword.getText()
                .equals(txtConfirmaPassword.getText())) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Contraseña",
                    "Contraseña no coincide"
            );
            return false;
        }
        if (rutaFoto == null) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.WARNING
                    ,"Fotografia"
                    ,"Debe seleccionar una fotografia"
            );
            return false;
        }
        return true;
    }

    @FXML
    private void regresar(ActionEvent event) {
        try {
            navegationService.cambiarVista(
                    "login.fxml",txtUsuario
            );
        }catch (Exception e){
           UtilController.mostrarException(e);
        }
    }
}
