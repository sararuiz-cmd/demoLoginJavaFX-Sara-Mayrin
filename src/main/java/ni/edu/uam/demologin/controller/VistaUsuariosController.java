package ni.edu.uam.demologin.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import ni.edu.uam.demologin.model.Usuario;

import java.awt.*;
import java.awt.Button;

public class VistaUsuariosController {
    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario, String> colCorreo;
    @FXML
    private TableColumn<Usuario, Void> colAcciones;

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        //Esto significa que si tienes un Usuario con nombre = "May", la celda mostrará “May”.
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        //Esto significa que si tienes un Usuario con apellido = "Tellez", la celda mostrará “Tellez”.
        //Es mas para poder motrar cada dato en la table view
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        // Columna de botones
        //Muestra cada una de las opciones para cada datos de la tableview
        //La ventaja es que como cada tableview puede ser distinta, es mejor desarrollar los botones en el Controller
        //setCellFactory define como se vera cada celda de esa columna para cada newTableCell
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
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
        private void  eliminarUsuario(Usuario usuario) {
                      //to do
            }
        private void abrirVentanaEdicion(Usuario usuario) {
                // To do
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
                    HBox box = new HBox();
                    setGraphic(box);
                }
            }
        });
    }

}
