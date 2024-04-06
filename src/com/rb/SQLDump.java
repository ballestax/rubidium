/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rb;


import com.rb.persistence.JDBC.JDBCDAOFactory;
import com.rb.persistence.dao.DAOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author ballestax
 */
public class SQLDump {

    public static final String INSERT_INTO = "INSERT INTO ";
    private static Aplication app;

    private SQLDump() {
    }

    public static SQLDump getInstance(Aplication app) {
        SQLDump.app = app;
        return SQLDumpHolder.INSTANCE;
    }

    private static class SQLDumpHolder {

        private static final SQLDump INSTANCE = new SQLDump();
    }

    public final ArrayList<String> getBackupTable(String tabla) throws DAOException, SQLException {        
        return backupTable(tabla);
    }

    private ArrayList<String> backupTable(String table) throws DAOException, SQLException {

        Connection connection = ((JDBCDAOFactory) JDBCDAOFactory.getInstance()).getDataSource().getConnection();
        ArrayList<Integer> tipos = new ArrayList<>();
        ArrayList<String> batch = new ArrayList<>();
        StringBuilder base = new StringBuilder();
        base.append(INSERT_INTO);
        base.append("`").append(table).append("` (");

        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getColumns(null, null, table, null);

        while (rs.next()) {
            String columna = rs.getString(4);
            int tipo = rs.getInt(5);
            base.append("`").append(columna).append("`,");
            tipos.add(tipo);
        }
        base.deleteCharAt(base.length() - 1);
        base.append(") VALUES(");
        rs.close();

        String retrieve = "SELECT * FROM " + table;
        PreparedStatement ps = connection.prepareStatement(retrieve);
        rs = ps.executeQuery();

        StringBuilder query;
        int ind = 0;
        while (rs.next()) {
            query = new StringBuilder(base.toString());
            for (int i = 0; i < tipos.size(); i++) {
                Integer TIPO = tipos.get(i);
                if (TIPO == java.sql.Types.VARCHAR || TIPO == java.sql.Types.TIMESTAMP) {
                    String string = rs.getString(++ind);
                    if(string==null){
                        string = "NULL";
                    }
                    string = string.replace("'", "");
//                    if ("null".equalsIgnoreCase(string)) {
//                        string = "NULL";
//                    }
                    query.append("'").append(string).append("',");
                } else if (TIPO == java.sql.Types.INTEGER || TIPO == java.sql.Types.NUMERIC) {
                    query.append(rs.getLong(++ind)).append(",");
                } else if (TIPO == java.sql.Types.LONGVARBINARY) {
                    query.append("0,");
                    ++ind;
                } else if (TIPO == java.sql.Types.DATE) {
                    query.append("'").append(rs.getDate(++ind)).append("'").append(",");
                } else {
                    query.append(rs.getString(++ind)).append(",");
                }
            }
            ind = 0;
            query.deleteCharAt(query.length() - 1);
            query.append(");");
            batch.add(query.toString());
//            query.delete(0, query.length());

        }
        rs.close();
        ps.close();
        connection.close();

        return batch;

    }
}
