package twoot.ap.database;

import java.sql.SQLException;

interface With<P> {
  void run(P stmt) throws SQLException;
}
