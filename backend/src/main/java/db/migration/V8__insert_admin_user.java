package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.PreparedStatement;

/**
 * Migration Java que insere o usuário administrador utilizando
 * o BCrypt do Java para gerar o hash da senha exatamente
 * como a aplicação faz em tempo de execução.
 *
 * Insere apenas se não existir um usuário com o mesmo email.
 */
public class V8__insert_admin_user extends BaseJavaMigration {
  @Override
  public void migrate(Context context) throws Exception {
    final String nome = "Admin";
    final String email = "admin@example.com";
    final String rawSenha = "123456";

    // Gera o hash usando BCrypt (mesmo algoritmo que a aplicação usa)
    final String hashed = new BCryptPasswordEncoder().encode(rawSenha);

    final String sql = "INSERT INTO usuarios (nome, email, senha, created_at) " +
      "SELECT ?, ?, ?, CURRENT_TIMESTAMP " +
      "WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = ?)";

    try (PreparedStatement st = context.getConnection().prepareStatement(sql)) {
      st.setString(1, nome);
      st.setString(2, email);
      st.setString(3, hashed);
      st.setString(4, email);
      st.execute();
    }
  }
}
