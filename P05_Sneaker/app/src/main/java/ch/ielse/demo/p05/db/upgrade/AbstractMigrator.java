package ch.ielse.demo.p05.db.upgrade;

import org.greenrobot.greendao.database.Database;

import java.util.Date;

public abstract class AbstractMigrator {

    public abstract void onUpgrade(Database db);

    protected static final void createTemporaryTable(Database db, String tableName, Date createDate) {
        final String ALTER_TABLE_RENAME = "ALTER TABLE \"%s\" RENAME TO \"_%s_old_%ts\"";
        db.execSQL(String.format(ALTER_TABLE_RENAME, tableName, tableName, createDate));
    }

    protected static final void dataTransferFromTemporary(Database db, String tableName, Date tempDate, String oldC, String newC) {
        final String DATA_TRANSFER = "INSERT INTO \"%s\" (%s) SELECT %s FROM \"_%s_old_%ts\"";
        db.execSQL(String.format(DATA_TRANSFER, tableName, newC, oldC, tableName, tempDate));
    }

    protected static final void dropTemporaryTable(Database db, String tableName, Date createDate) {
        final String DROP_TABLE = "DROP TABLE \"_%s_old_%ts\"";
        db.execSQL(String.format(DROP_TABLE, tableName, createDate));
    }
}
