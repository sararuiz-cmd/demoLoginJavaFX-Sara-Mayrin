package ni.edu.uam.demologin.repository;

import ni.edu.uam.demologin.config.IConnection;
import ni.edu.uam.demologin.config.PostgreSQLConnection;
import ni.edu.uam.demologin.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioRepositoryJdbc implements UsuarioRepository{

    private final IConnection connection;

    @Override
    public List<Usuario> findAll() throws Exception{
        String sql = "SELECT id" +
                     ",primer_nombre" +
                     ",primer_apellido" +
                     ",ruta_foto" +
                     ",usuario" +
                     ",correo" +
                     ",password " +
                     "from usuario " +
                     " order by primer_nombre asc";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getString("primer_nombre"),
                        rs.getString("primer_apellido"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("usuario"),
                        rs.getString("ruta_foto")
                );
                usuario.setId(rs.getObject("id", UUID.class));
                usuarios.add(usuario);
            }
            return usuarios;
        }
    }

    public UsuarioRepositoryJdbc(IConnection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<Usuario> findById(String id) throws Exception{
        String sql = "SELECT id" +
                     ",primer_nombre" +
                     ",primer_apellido" +
                     ",ruta_foto" +
                     ",usuario" +
                     ",correo" +
                     ",password " +
                     "from usuario " +
                     " where id = ?";
        try(Connection con= connection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setObject(1,UUID.fromString(id));
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                            rs.getString("primer_nombre"),
                            rs.getString("primer_apellido"),
                            rs.getString("correo"),
                            rs.getString("password"),
                            rs.getString("usuario"),
                            rs.getString("ruta_foto")
                    );
                    usuario.setId(rs.getObject("id", UUID.class));
                    return Optional.of(usuario);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void save(Usuario usuario) throws Exception {
        String sql = "INSERT INTO usuario " +
                     "(id," +
                     "primer_nombre," +
                     "primer_apellido," +
                     "usuario," +
                     "password," +
                     "correo," +
                     "ruta_foto)" +
                     "VALUES(?,?,?,?,?,?,?)";
        try(Connection con= connection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setObject(1, usuario.getId());
            ps.setString(2,usuario.getNombre());
            ps.setString(3,usuario.getApellido());
            ps.setString(4,usuario.getUsuario());
            ps.setString(5,usuario.getPassword());
            ps.setString(6,usuario.getCorreo());
            ps.setString(7,usuario.getRutafoto());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Usuario usuario) throws Exception {
        String sql = "UPDATE usuario SET " +
                     "primer_nombre = ?," +
                     "primer_apellido = ?," +
                     "usuario = ?," +
                     "password = ?," +
                     "correo = ?," +
                     "ruta_foto = ? " +
                     "WHERE id = ?";
        try(Connection con= connection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setString(1,usuario.getNombre());
            ps.setString(2,usuario.getApellido());
            ps.setString(3,usuario.getUsuario());
            ps.setString(4,usuario.getPassword());
            ps.setString(5,usuario.getCorreo());
            ps.setString(6,usuario.getRutafoto());
            ps.setObject(7,usuario.getId());
            if (ps.executeUpdate() == 0) {
                throw new Exception("No se encontró el usuario a actualizar");
            }
        }
    }

    @Override
    public void delete(UUID id) throws Exception {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try(Connection con= connection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setObject(1,id);
            if (ps.executeUpdate() == 0) {
                throw new Exception("No se encontró el usuario a eliminar");
            }
        }
    }

    @Override
    public Optional<Usuario> findByLogin(String usuario
            , String password) throws Exception {
        final String sql = " SELECT id,usuario,correo,password FROM public.usuario WHERE usuario = ? AND password = ? ";
        try(Connection con= connection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);)
        {
            ps.setString(1,usuario);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setUsuario(rs.getString("usuario"));
                u.setPassword(rs.getString("password"));
                u.setCorreo(rs.getString("correo"));
                u.setId(rs.getObject("id",UUID.class));
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }
}
