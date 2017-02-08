package com.tn.demineurmadjoh.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tn.demineurmadjoh.helper.DmDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//Enregistrer le carrelage de jeux
public class DmTile extends DmObject {
    public final static String DB_TABLE_NAME = "tiles";
    public final static String PARAM_KEY_ROW_INDEX = "row_index"; // zero-based
    public final static String PARAM_KEY_COL_INDEX = "col_index"; // zero-based
    public final static String PARAM_KEY_IS_EXPLORED = "is_explored";
    public final static String PARAM_KEY_IS_FLAGGED = "is_flagged";
    public final static String PARAM_KEY_HAS_MINE = "has_mine";
    public final static String PARAM_KEY_ADJACENT_MINES = "adjacent_mines";

    private Integer mId = null;
    private Integer mRowIndex = null;
    private Integer mColIndex = null;
    private Boolean mIsExplored = null;
    private Boolean mIsFlagged = null;
    private Boolean mHasMine = null;
    private Integer mAdjacentMines = null;

    public DmTile(Integer id,
                  Integer rowIndex,
                  Integer colIndex,
                  Boolean isExplored,
                  Boolean isFlagged,
                  Boolean hasMine,
                  Integer adjacentMines) {
        super();

        setId(id);
        setRowIndex(rowIndex);
        setColIndex(colIndex);
        setIsExplored(isExplored);
        setIsFlagged(isFlagged);
        setHasMine(hasMine);
        setAdjacentMines(adjacentMines);
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ", %s real" +
                        ");",
                DB_TABLE_NAME,
                PARAM_KEY_ID,
                PARAM_KEY_ROW_INDEX,
                PARAM_KEY_COL_INDEX,
                PARAM_KEY_IS_EXPLORED,
                PARAM_KEY_IS_FLAGGED,
                PARAM_KEY_HAS_MINE,
                PARAM_KEY_ADJACENT_MINES));
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL(String.format("drop table if exists %s", DB_TABLE_NAME));
    }

    public static List<DmTile> loadTiles(DmDatabaseHelper dbHelper) {
        ArrayList<DmTile> tiles = new ArrayList<>();
        Cursor result = dbHelper.getReadableDatabase()
                .rawQuery(String.format("select %s" +
                                ",  %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                " from %s",
                        PARAM_KEY_ID,
                        PARAM_KEY_ROW_INDEX,
                        PARAM_KEY_COL_INDEX,
                        PARAM_KEY_IS_EXPLORED,
                        PARAM_KEY_IS_FLAGGED,
                        PARAM_KEY_HAS_MINE,
                        PARAM_KEY_ADJACENT_MINES,
                        DB_TABLE_NAME), null);
        while (result.moveToNext()) {
            tiles.add(new DmTile(
                    result.getInt(0),
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3) == 1,
                    result.getInt(4) == 1,
                    result.getInt(5) == 1,
                    result.getInt(6)
            ));
        }
        result.close();
        return tiles;
    }

    public static List<DmTile> generateTiles(DmDatabaseHelper dbHelper, int dimension, int mines) {
        dbHelper.getWritableDatabase().delete(DB_TABLE_NAME, null, null);

        ArrayList<DmTile> tiles = new ArrayList<>();
        for (int i = 0, k = 1; i < dimension; i++) {
            for (int j = 0; j < dimension; j++, k++) {
                DmTile tile = new DmTile(k, i, j, false, false, false, 0);
                tiles.add(tile);
                tile.insert(dbHelper);
            }
        }
        return tiles;
    }

    public static int getNumberOfUnexploredTiles(DmDatabaseHelper dbHelper) {
        int num = 0;
        Cursor result = dbHelper.getReadableDatabase()
                .rawQuery(String.format("select 1 from %s where %s = 0",
                        DB_TABLE_NAME,
                        PARAM_KEY_IS_EXPLORED), null);
        while (result.moveToNext()) {
            num++;
        }
        result.close();
        return num;
    }

    public static List<DmTile> loadUnexploredMineFreeTiles(DmDatabaseHelper dbHelper) {
        ArrayList<DmTile> tiles = new ArrayList<>();
        Cursor result = dbHelper.getReadableDatabase()
                .rawQuery(String.format("select %s" +
                                ",  %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                ", %s" +
                                " from %s where %s = 0 and %s = 0",
                        PARAM_KEY_ID,
                        PARAM_KEY_ROW_INDEX,
                        PARAM_KEY_COL_INDEX,
                        PARAM_KEY_IS_EXPLORED,
                        PARAM_KEY_IS_FLAGGED,
                        PARAM_KEY_HAS_MINE,
                        PARAM_KEY_ADJACENT_MINES,
                        DB_TABLE_NAME,
                        PARAM_KEY_IS_EXPLORED,
                        PARAM_KEY_HAS_MINE), null);
        while (result.moveToNext()) {
            tiles.add(new DmTile(
                    result.getInt(0),
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3) == 1,
                    result.getInt(4) == 1,
                    result.getInt(5) == 1,
                    result.getInt(6)
            ));
        }
        result.close();
        return tiles;
    }

    public void insertOrUpdate(DmDatabaseHelper dbHelper, boolean bUpdate) {
        ContentValues cv = new ContentValues();
        cv.put(PARAM_KEY_ID, getId());
        cv.put(PARAM_KEY_ROW_INDEX, getRowIndex());
        cv.put(PARAM_KEY_COL_INDEX, getColIndex());
        cv.put(PARAM_KEY_IS_EXPLORED, getIsExplored());
        cv.put(PARAM_KEY_IS_FLAGGED, getIsFlagged());
        cv.put(PARAM_KEY_HAS_MINE, getHasMine());
        cv.put(PARAM_KEY_ADJACENT_MINES, getAdjacentMines());
        if (bUpdate) {
            dbHelper.getWritableDatabase().update(DB_TABLE_NAME, cv, PARAM_KEY_ID + " = ?", new String[]{String.valueOf(getId())});
        } else {
            dbHelper.getWritableDatabase().insert(DB_TABLE_NAME, PARAM_KEY_ID, cv);
        }
    }

    public void insert(DmDatabaseHelper dbHelper) {
        insertOrUpdate(dbHelper, false);
    }

    public void saveChanges(DmDatabaseHelper dbHelper) {
        insertOrUpdate(dbHelper, true);
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getRowIndex() {
        return mRowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        mRowIndex = rowIndex;
    }

    public Integer getColIndex() {
        return mColIndex;
    }

    public void setColIndex(Integer colIndex) {
        mColIndex = colIndex;
    }

    public Boolean getIsExplored() {
        return mIsExplored;
    }

    public void setIsExplored(Boolean isExplored) {
        mIsExplored = isExplored;
    }

    public Boolean getIsFlagged() {
        return mIsFlagged;
    }

    public void setIsFlagged(Boolean isFlagged) {
        mIsFlagged = isFlagged;
    }

    public Boolean getHasMine() {
        return mHasMine;
    }

    public void setHasMine(Boolean hasMine) {
        mHasMine = hasMine;
    }

    public Integer getAdjacentMines() {
        return mAdjacentMines;
    }

    public void setAdjacentMines(Integer adjacentMines) {
        mAdjacentMines = adjacentMines;
    }

    @Override
    public HashMap<String, String> toDict() {
        HashMap<String, String> obj = super.toDict();
        obj.put(PARAM_KEY_ID, getId() != null ? getId().toString() : null);
        obj.put(PARAM_KEY_ROW_INDEX, getRowIndex() != null ? getRowIndex().toString() : null);
        obj.put(PARAM_KEY_COL_INDEX, getColIndex() != null ? getColIndex().toString() : null);
        obj.put(PARAM_KEY_IS_EXPLORED, getIsExplored() != null ? getIsExplored().toString() : null);
        obj.put(PARAM_KEY_IS_FLAGGED, getIsFlagged() != null ? getIsFlagged().toString() : null);
        obj.put(PARAM_KEY_HAS_MINE, getHasMine() != null ? getHasMine().toString() : null);
        obj.put(PARAM_KEY_ADJACENT_MINES, getAdjacentMines() != null ? getAdjacentMines().toString() : null);
        return obj;
    }
}
