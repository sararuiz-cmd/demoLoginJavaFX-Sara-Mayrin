package ni.edu.uam.demologin.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import ni.edu.uam.demologin.model.Usuario;
import ni.edu.uam.demologin.service.ArchivoService;
import ni.edu.uam.demologin.service.NavegationService;
import ni.edu.uam.demologin.service.UsuarioService;

import java.io.File;
import java.util.Optional;

public class VistaUsuariosController {
    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario, String> colUsuario;
    @FXML
    private TableColumn<Usuario, String> colCorreo;
    @FXML
    private TableColumn<Usuario, Void> colAcciones;

    private final UsuarioService service = new UsuarioService();
    private final NavegationService
            navegationService = new NavegationService();
    private final ArchivoService
            archivoService = new ArchivoService();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        //Esto significa que si tienes un Usuario con nombre = "May", la celda mostrará “May”.
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        //Esto significa que si tienes un Usuario con apellido = "Tellez", la celda mostrará “Tellez”.
        //Es mas para poder motrar cada dato en la table view
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        // Columna de botones
        //Muestra cada una de las opciones para cada datos de la tableview
        //La ventaja es que como cada tableview puede ser distinta, es mejor desarrollar los botones en el Controller
        //setCellFactory define como se vera cada celda de esa columna para cada newTableCell
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox box = new HBox(5, btnEditar, btnEliminar);

            {
                btnEliminar.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
                //getIndex obtiene el indice de donde se encuentra actualemte
                //getTableView().getItems().get(getIndex()) → recupera el objeto Usuario de esa fila.
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    abrirVentanaEdicion(usuario);
                });

                btnEliminar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Eliminacion de usuarios");
                    confirm.setHeaderText("¿Eliminar usuario?");
                    confirm.setContentText("Usuario: " + usuario.getNombre());
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            eliminarUsuario(usuario);
                        }
                    });
                });
            }

            //Este metodo se ejecuta cada vez que la tabla dibuja una celda.
            //si empty, no muestra nada
            //Si hay user, coloca los botones
            //Es como un verificador para saber en que columnas poner botones
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        cargarUsuarios();
    }

    @FXML
    private void cargarUsuarios() {
        try {
            tablaUsuarios.setItems(
                    FXCollections.observableArrayList(service.findAll())
            );
        } catch (Exception e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar los usuarios: " + e.getMessage()
            );
        }
    }

    @FXML
    private void agregarUsuario(ActionEvent event) {
        mostrarFormulario(null).ifPresent(usuario -> {
            try {
                service.save(usuario);
                cargarUsuarios();
                UtilController.mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Éxito",
                        "Usuario agregado correctamente"
                );
            } catch (Exception e) {
                UtilController.mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo agregar el usuario: " + e.getMessage()
                );
            }
        });
    }

    private void abrirVentanaEdicion(Usuario usuario) {
        mostrarFormulario(usuario).ifPresent(editado -> {
            try {
                service.update(editado);
                cargarUsuarios();
                UtilController.mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Éxito",
                        "Usuario actualizado correctamente"
                );
            } catch (Exception e) {
                UtilController.mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo actualizar el usuario: " + e.getMessage()
                );
            }
        });
    }

    private void eliminarUsuario(Usuario usuario) {
        try {
            service.delete(usuario.getId());
            cargarUsuarios();
            UtilController.mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Usuario eliminado correctamente"
            );
        } catch (Exception e) {
            UtilController.mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo eliminar el usuario: " + e.getMessage()
            );
        }
    }

    //Formulario para agregar (usuario == null) o editar un usuario existente
    private Optional<Usuario> mostrarFormulario(Usuario usuario) {
        boolean esNuevo = usuario == null;

        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(esNuevo ? "Agregar usuario" : "Actualizar usuario");
        dialog.setHeaderText(null);
        dialog.initOwner(tablaUsuarios.getScene().getWindow());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        TextField txtNombre = new TextField();
        TextField txtApellido = new TextField();
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("correo@ejemplo.com");
        TextField txtUsuario = new TextField();
        PasswordField txtPassword = new PasswordField();

        if (!esNuevo) {
            txtNombre.setText(usuario.getNombre());
            txtApellido.setText(usuario.getApellido());
            txtCorreo.setText(usuario.getCorreo());
            txtUsuario.setText(usuario.getUsuario());
            txtPassword.setText(usuario.getPassword());
        }

        //Arreglo de un elemento para poder modificarlo dentro de las lambdas
        String[] rutaFoto = { esNuevo ? null : usuario.getRutafoto() };
        ImageView imageFoto = new ImageView();
        imageFoto.setFitWidth(120);
        imageFoto.setFitHeight(120);
        imageFoto.setPreserveRatio(true);
        mostrarImagen(imageFoto, rutaFoto[0]);

        Button btnFoto = new Button("Seleccionar foto");
        btnFoto.setOnAction(e -> {
            File archivo = seleccionarArchivo(btnFoto);
            if (archivo == null) {
                return;
            }
            try {
                rutaFoto[0] = archivoService.guardarArchivo(archivo);
                mostrarImagen(imageFoto, rutaFoto[0]);
            } catch (Exception ex) {
                UtilController.mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Error",
                        "No se pudo guardar la fotografia: " + ex.getMessage()
                );
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, new Label("Nombre"), txtNombre);
        grid.addRow(1, new Label("Apellido"), txtApellido);
        grid.addRow(2, new Label("Correo"), txtCorreo);
        grid.addRow(3, new Label("Usuario"), txtUsuario);
        grid.addRow(4, new Label("Contraseña"), txtPassword);
        grid.addRow(5, new Label("Fotografia"), btnFoto);
        grid.add(imageFoto, 1, 6);
        dialog.getDialogPane().setContent(grid);

        //Valida antes de cerrar; si falla, consume el evento y el dialogo sigue abierto
        Node botonGuardar = dialog.getDialogPane().lookupButton(btnGuardar);
        botonGuardar.addEventFilter(ActionEvent.ACTION, event -> {
            if (txtNombre.getText().isBlank()
                || txtApellido.getText().isBlank()
                || txtCorreo.getText().isBlank()
                || txtUsuario.getText().isBlank()
                || txtPassword.getText().isBlank()
            ) {
                UtilController.mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Validacion",
                        "Todos los campos son obligatorios"
                );
                event.consume();
            }
            else if (!txtCorreo.getText().matches("^[^@\\s]+@[^@\\s]+$")) {
                UtilController.mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Validacion",
                        "El correo no tiene un formato válido"
                );
                event.consume();
            }
            else if (rutaFoto[0] == null || rutaFoto[0].isBlank()) {
                UtilController.mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Fotografia",
                        "Debe seleccionar una fotografia"
                );
                event.consume();
            }
        });

        dialog.setResultConverter(boton -> {
            if (boton != btnGuardar) {
                return null;
            }
            //Se trabaja sobre una copia para no alterar la fila si la BD falla
            Usuario resultado = new Usuario();
            if (!esNuevo) {
                resultado.setId(usuario.getId());
            }
            resultado.setRutafoto(rutaFoto[0]);
            resultado.setNombre(txtNombre.getText().trim());
            resultado.setApellido(txtApellido.getText().trim());
            resultado.setCorreo(txtCorreo.getText().trim());
            resultado.setUsuario(txtUsuario.getText().trim());
            resultado.setPassword(txtPassword.getText());
            return resultado;
        });

        return dialog.showAndWait();
    }

    private File seleccionarArchivo(Node owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo");
        fc.getExtensionFilters()
                .add(new FileChooser
                        .ExtensionFilter("Imagenes"
                        , "*.jpg"
                        , "*.png", "*.jpeg"));
        return fc.showOpenDialog(owner.getScene().getWindow());
    }

    private void mostrarImagen(ImageView imageView, String rutaFoto) {
        if (rutaFoto == null || rutaFoto.isBlank()
            || !new File(rutaFoto).exists()) {
            imageView.setImage(null);
            return;
        }
        imageView.setImage(new Image(
                new File(rutaFoto).toURI().toString()
        ));
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            navegationService.cambiarVista("login.fxml", tablaUsuarios);
        } catch (Exception e) {
            UtilController.mostrarException(e);
        }
    }
}
