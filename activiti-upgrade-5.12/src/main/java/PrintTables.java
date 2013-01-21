/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.HashSet;

import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.database.structure.Table;
import liquibase.diff.DiffStatusListener;
import liquibase.exception.DatabaseException;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.DatabaseSnapshotGeneratorFactory;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Small help tool to see what tables will be deleted by liquibase.
 * Interesting if for example you are using an admin user to connect to the database,
 * and you're worrying you might delete some other important tables.
 * 
 * Run 'mvn compile' and 'mvn exec:java -Dexec.mainClass="PrintTables"'
 * 
 * @author jbarrez
 */
public class PrintTables {
  
  private static final Logger LOG = LoggerFactory.getLogger(PrintTables.class);

  public static void main(String[] args) {
    
    LOG.info("POSTGRES");
    printTables("org.postgresql.Driver", "jdbc:postgresql://pjnk01.alfresco.com:5432/activitiupgrade", "activiti", "activiti");
    
    LOG.info("H2");
    printTables("org.h2.Driver", "jdbc:h2:tcp://localhost/activiti", "sa", "");
    
    LOG.info("MYSQL");
    printTables("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/activitiupgrade?autoReconnect=true", "activitiupgrade", "activitiupgrade");
    
    LOG.info("DB2");
    printTables("com.ibm.db2.jcc.DB2Driver", "jdbc:db2://172.30.40.228:50000/aupgrade", "db2admin", "db2admin");
    
    LOG.info("MSSQL");
    printTables("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://172.30.40.227;databaseName=activitiupgrade", "alfresco", "alfresco");
    
    LOG.info("ORACLE");
    printTables("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@pjnk01.alfresco.com:1521:xe", "activitiupgrade", "activiti");

    LOG.info("Done");
  }

  protected static void printTables(String driver, String url, String user, String password) {
    PooledDataSource dataSource = new PooledDataSource(driver, url, user, password);
    DatabaseConnection connection = null;
    Database database = null;
    try {
      connection = new JdbcConnection(dataSource.getConnection());
      database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
      DatabaseSnapshot snapshot = DatabaseSnapshotGeneratorFactory.getInstance().createSnapshot(database, database.getDefaultSchemaName(), new HashSet<DiffStatusListener>());
      for (Table table : snapshot.getTables()) {
        LOG.info("Found table : " + table.getName());
      }
      
    } catch (Exception exception) {
      exception.printStackTrace();

      if (connection != null) {
        try {
          connection.close();
        } catch (DatabaseException e) {
          e.printStackTrace();
        }
      }

      if (database != null) {
        try {
          database.close();
        } catch (DatabaseException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
