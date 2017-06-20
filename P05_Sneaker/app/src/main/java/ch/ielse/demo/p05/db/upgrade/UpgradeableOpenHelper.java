package ch.ielse.demo.p05.db.upgrade;

import android.content.Context;

import ch.ielse.demo.p05.Const;
import ch.ielse.demo.p05.db.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class UpgradeableOpenHelper extends DaoMaster.OpenHelper {

    public UpgradeableOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Const.INSTANCE.logd("greenDAO Upgrading schema from version %d to %d", oldVersion, newVersion);
        for (int i = oldVersion; i < newVersion; i++) {
            try {
                AbstractMigrator migratorHelper =
                        (AbstractMigrator) Class.forName(
                                AbstractMigrator.class.getPackage().getName() + ".Migrator" + (i + 1)).newInstance();
                if (migratorHelper != null) {
                    migratorHelper.onUpgrade(db);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Const.INSTANCE.loge("greenDAO Could not migrate from schema from schema: %d to %d", i, i + 1);
                break;
            }
        }
    }
}