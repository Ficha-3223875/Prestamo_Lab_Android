package com.example.prestamolab.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SolicitudDao_Impl implements SolicitudDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SolicitudEntity> __insertionAdapterOfSolicitudEntity;

  private final EntityDeletionOrUpdateAdapter<SolicitudEntity> __updateAdapterOfSolicitudEntity;

  private final SharedSQLiteStatement __preparedStmtOfActualizarEstado;

  private final SharedSQLiteStatement __preparedStmtOfActualizarEvidencia;

  private final SharedSQLiteStatement __preparedStmtOfActualizarEstadoEvidencia;

  private final SharedSQLiteStatement __preparedStmtOfActualizarSincronizado;

  public SolicitudDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSolicitudEntity = new EntityInsertionAdapter<SolicitudEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `solicitudes` (`id`,`equipoId`,`ambienteDestino`,`proposito`,`duracionHoras`,`estado`,`fechaSolicitud`,`fechaLimiteDevolucion`,`evidenciaUri`,`evidenciaEstado`,`latitud`,`longitud`,`sincronizado`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SolicitudEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEquipoId());
        statement.bindString(3, entity.getAmbienteDestino());
        statement.bindString(4, entity.getProposito());
        statement.bindLong(5, entity.getDuracionHoras());
        statement.bindString(6, entity.getEstado());
        statement.bindLong(7, entity.getFechaSolicitud());
        statement.bindLong(8, entity.getFechaLimiteDevolucion());
        if (entity.getEvidenciaUri() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getEvidenciaUri());
        }
        statement.bindString(10, entity.getEvidenciaEstado());
        if (entity.getLatitud() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getLatitud());
        }
        if (entity.getLongitud() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getLongitud());
        }
        final int _tmp = entity.getSincronizado() ? 1 : 0;
        statement.bindLong(13, _tmp);
      }
    };
    this.__updateAdapterOfSolicitudEntity = new EntityDeletionOrUpdateAdapter<SolicitudEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `solicitudes` SET `id` = ?,`equipoId` = ?,`ambienteDestino` = ?,`proposito` = ?,`duracionHoras` = ?,`estado` = ?,`fechaSolicitud` = ?,`fechaLimiteDevolucion` = ?,`evidenciaUri` = ?,`evidenciaEstado` = ?,`latitud` = ?,`longitud` = ?,`sincronizado` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SolicitudEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEquipoId());
        statement.bindString(3, entity.getAmbienteDestino());
        statement.bindString(4, entity.getProposito());
        statement.bindLong(5, entity.getDuracionHoras());
        statement.bindString(6, entity.getEstado());
        statement.bindLong(7, entity.getFechaSolicitud());
        statement.bindLong(8, entity.getFechaLimiteDevolucion());
        if (entity.getEvidenciaUri() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getEvidenciaUri());
        }
        statement.bindString(10, entity.getEvidenciaEstado());
        if (entity.getLatitud() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getLatitud());
        }
        if (entity.getLongitud() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getLongitud());
        }
        final int _tmp = entity.getSincronizado() ? 1 : 0;
        statement.bindLong(13, _tmp);
        statement.bindLong(14, entity.getId());
      }
    };
    this.__preparedStmtOfActualizarEstado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE solicitudes SET estado = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActualizarEvidencia = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE solicitudes SET evidenciaUri = ?, evidenciaEstado = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActualizarEstadoEvidencia = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE solicitudes SET evidenciaEstado = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActualizarSincronizado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE solicitudes SET sincronizado = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertar(final SolicitudEntity solicitud,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSolicitudEntity.insertAndReturnId(solicitud);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizar(final SolicitudEntity solicitud,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSolicitudEntity.handle(solicitud);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizarEstado(final int id, final String estado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarEstado.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, estado);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarEstado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizarEvidencia(final int id, final String uri, final String estado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarEvidencia.acquire();
        int _argIndex = 1;
        if (uri == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, uri);
        }
        _argIndex = 2;
        _stmt.bindString(_argIndex, estado);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarEvidencia.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizarEstadoEvidencia(final int id, final String estado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarEstadoEvidencia.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, estado);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarEstadoEvidencia.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizarSincronizado(final int id, final boolean sincronizado,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarSincronizado.acquire();
        int _argIndex = 1;
        final int _tmp = sincronizado ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarSincronizado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SolicitudEntity>> observarTodas() {
    final String _sql = "SELECT * FROM solicitudes ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"solicitudes"}, new Callable<List<SolicitudEntity>>() {
      @Override
      @NonNull
      public List<SolicitudEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEquipoId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipoId");
          final int _cursorIndexOfAmbienteDestino = CursorUtil.getColumnIndexOrThrow(_cursor, "ambienteDestino");
          final int _cursorIndexOfProposito = CursorUtil.getColumnIndexOrThrow(_cursor, "proposito");
          final int _cursorIndexOfDuracionHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionHoras");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfFechaSolicitud = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaSolicitud");
          final int _cursorIndexOfFechaLimiteDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaLimiteDevolucion");
          final int _cursorIndexOfEvidenciaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaUri");
          final int _cursorIndexOfEvidenciaEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaEstado");
          final int _cursorIndexOfLatitud = CursorUtil.getColumnIndexOrThrow(_cursor, "latitud");
          final int _cursorIndexOfLongitud = CursorUtil.getColumnIndexOrThrow(_cursor, "longitud");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final List<SolicitudEntity> _result = new ArrayList<SolicitudEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SolicitudEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpEquipoId;
            _tmpEquipoId = _cursor.getInt(_cursorIndexOfEquipoId);
            final String _tmpAmbienteDestino;
            _tmpAmbienteDestino = _cursor.getString(_cursorIndexOfAmbienteDestino);
            final String _tmpProposito;
            _tmpProposito = _cursor.getString(_cursorIndexOfProposito);
            final int _tmpDuracionHoras;
            _tmpDuracionHoras = _cursor.getInt(_cursorIndexOfDuracionHoras);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpFechaSolicitud;
            _tmpFechaSolicitud = _cursor.getLong(_cursorIndexOfFechaSolicitud);
            final long _tmpFechaLimiteDevolucion;
            _tmpFechaLimiteDevolucion = _cursor.getLong(_cursorIndexOfFechaLimiteDevolucion);
            final String _tmpEvidenciaUri;
            if (_cursor.isNull(_cursorIndexOfEvidenciaUri)) {
              _tmpEvidenciaUri = null;
            } else {
              _tmpEvidenciaUri = _cursor.getString(_cursorIndexOfEvidenciaUri);
            }
            final String _tmpEvidenciaEstado;
            _tmpEvidenciaEstado = _cursor.getString(_cursorIndexOfEvidenciaEstado);
            final Double _tmpLatitud;
            if (_cursor.isNull(_cursorIndexOfLatitud)) {
              _tmpLatitud = null;
            } else {
              _tmpLatitud = _cursor.getDouble(_cursorIndexOfLatitud);
            }
            final Double _tmpLongitud;
            if (_cursor.isNull(_cursorIndexOfLongitud)) {
              _tmpLongitud = null;
            } else {
              _tmpLongitud = _cursor.getDouble(_cursorIndexOfLongitud);
            }
            final boolean _tmpSincronizado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp != 0;
            _item = new SolicitudEntity(_tmpId,_tmpEquipoId,_tmpAmbienteDestino,_tmpProposito,_tmpDuracionHoras,_tmpEstado,_tmpFechaSolicitud,_tmpFechaLimiteDevolucion,_tmpEvidenciaUri,_tmpEvidenciaEstado,_tmpLatitud,_tmpLongitud,_tmpSincronizado);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<SolicitudEntity> observarPorId(final int id) {
    final String _sql = "SELECT * FROM solicitudes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"solicitudes"}, new Callable<SolicitudEntity>() {
      @Override
      @Nullable
      public SolicitudEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEquipoId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipoId");
          final int _cursorIndexOfAmbienteDestino = CursorUtil.getColumnIndexOrThrow(_cursor, "ambienteDestino");
          final int _cursorIndexOfProposito = CursorUtil.getColumnIndexOrThrow(_cursor, "proposito");
          final int _cursorIndexOfDuracionHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionHoras");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfFechaSolicitud = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaSolicitud");
          final int _cursorIndexOfFechaLimiteDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaLimiteDevolucion");
          final int _cursorIndexOfEvidenciaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaUri");
          final int _cursorIndexOfEvidenciaEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaEstado");
          final int _cursorIndexOfLatitud = CursorUtil.getColumnIndexOrThrow(_cursor, "latitud");
          final int _cursorIndexOfLongitud = CursorUtil.getColumnIndexOrThrow(_cursor, "longitud");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final SolicitudEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpEquipoId;
            _tmpEquipoId = _cursor.getInt(_cursorIndexOfEquipoId);
            final String _tmpAmbienteDestino;
            _tmpAmbienteDestino = _cursor.getString(_cursorIndexOfAmbienteDestino);
            final String _tmpProposito;
            _tmpProposito = _cursor.getString(_cursorIndexOfProposito);
            final int _tmpDuracionHoras;
            _tmpDuracionHoras = _cursor.getInt(_cursorIndexOfDuracionHoras);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpFechaSolicitud;
            _tmpFechaSolicitud = _cursor.getLong(_cursorIndexOfFechaSolicitud);
            final long _tmpFechaLimiteDevolucion;
            _tmpFechaLimiteDevolucion = _cursor.getLong(_cursorIndexOfFechaLimiteDevolucion);
            final String _tmpEvidenciaUri;
            if (_cursor.isNull(_cursorIndexOfEvidenciaUri)) {
              _tmpEvidenciaUri = null;
            } else {
              _tmpEvidenciaUri = _cursor.getString(_cursorIndexOfEvidenciaUri);
            }
            final String _tmpEvidenciaEstado;
            _tmpEvidenciaEstado = _cursor.getString(_cursorIndexOfEvidenciaEstado);
            final Double _tmpLatitud;
            if (_cursor.isNull(_cursorIndexOfLatitud)) {
              _tmpLatitud = null;
            } else {
              _tmpLatitud = _cursor.getDouble(_cursorIndexOfLatitud);
            }
            final Double _tmpLongitud;
            if (_cursor.isNull(_cursorIndexOfLongitud)) {
              _tmpLongitud = null;
            } else {
              _tmpLongitud = _cursor.getDouble(_cursorIndexOfLongitud);
            }
            final boolean _tmpSincronizado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp != 0;
            _result = new SolicitudEntity(_tmpId,_tmpEquipoId,_tmpAmbienteDestino,_tmpProposito,_tmpDuracionHoras,_tmpEstado,_tmpFechaSolicitud,_tmpFechaLimiteDevolucion,_tmpEvidenciaUri,_tmpEvidenciaEstado,_tmpLatitud,_tmpLongitud,_tmpSincronizado);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object obtenerPorId(final int id,
      final Continuation<? super SolicitudEntity> $completion) {
    final String _sql = "SELECT * FROM solicitudes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SolicitudEntity>() {
      @Override
      @Nullable
      public SolicitudEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEquipoId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipoId");
          final int _cursorIndexOfAmbienteDestino = CursorUtil.getColumnIndexOrThrow(_cursor, "ambienteDestino");
          final int _cursorIndexOfProposito = CursorUtil.getColumnIndexOrThrow(_cursor, "proposito");
          final int _cursorIndexOfDuracionHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionHoras");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfFechaSolicitud = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaSolicitud");
          final int _cursorIndexOfFechaLimiteDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaLimiteDevolucion");
          final int _cursorIndexOfEvidenciaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaUri");
          final int _cursorIndexOfEvidenciaEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaEstado");
          final int _cursorIndexOfLatitud = CursorUtil.getColumnIndexOrThrow(_cursor, "latitud");
          final int _cursorIndexOfLongitud = CursorUtil.getColumnIndexOrThrow(_cursor, "longitud");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final SolicitudEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpEquipoId;
            _tmpEquipoId = _cursor.getInt(_cursorIndexOfEquipoId);
            final String _tmpAmbienteDestino;
            _tmpAmbienteDestino = _cursor.getString(_cursorIndexOfAmbienteDestino);
            final String _tmpProposito;
            _tmpProposito = _cursor.getString(_cursorIndexOfProposito);
            final int _tmpDuracionHoras;
            _tmpDuracionHoras = _cursor.getInt(_cursorIndexOfDuracionHoras);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpFechaSolicitud;
            _tmpFechaSolicitud = _cursor.getLong(_cursorIndexOfFechaSolicitud);
            final long _tmpFechaLimiteDevolucion;
            _tmpFechaLimiteDevolucion = _cursor.getLong(_cursorIndexOfFechaLimiteDevolucion);
            final String _tmpEvidenciaUri;
            if (_cursor.isNull(_cursorIndexOfEvidenciaUri)) {
              _tmpEvidenciaUri = null;
            } else {
              _tmpEvidenciaUri = _cursor.getString(_cursorIndexOfEvidenciaUri);
            }
            final String _tmpEvidenciaEstado;
            _tmpEvidenciaEstado = _cursor.getString(_cursorIndexOfEvidenciaEstado);
            final Double _tmpLatitud;
            if (_cursor.isNull(_cursorIndexOfLatitud)) {
              _tmpLatitud = null;
            } else {
              _tmpLatitud = _cursor.getDouble(_cursorIndexOfLatitud);
            }
            final Double _tmpLongitud;
            if (_cursor.isNull(_cursorIndexOfLongitud)) {
              _tmpLongitud = null;
            } else {
              _tmpLongitud = _cursor.getDouble(_cursorIndexOfLongitud);
            }
            final boolean _tmpSincronizado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp != 0;
            _result = new SolicitudEntity(_tmpId,_tmpEquipoId,_tmpAmbienteDestino,_tmpProposito,_tmpDuracionHoras,_tmpEstado,_tmpFechaSolicitud,_tmpFechaLimiteDevolucion,_tmpEvidenciaUri,_tmpEvidenciaEstado,_tmpLatitud,_tmpLongitud,_tmpSincronizado);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object porEquipo(final int equipoId,
      final Continuation<? super List<SolicitudEntity>> $completion) {
    final String _sql = "SELECT * FROM solicitudes WHERE equipoId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, equipoId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SolicitudEntity>>() {
      @Override
      @NonNull
      public List<SolicitudEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEquipoId = CursorUtil.getColumnIndexOrThrow(_cursor, "equipoId");
          final int _cursorIndexOfAmbienteDestino = CursorUtil.getColumnIndexOrThrow(_cursor, "ambienteDestino");
          final int _cursorIndexOfProposito = CursorUtil.getColumnIndexOrThrow(_cursor, "proposito");
          final int _cursorIndexOfDuracionHoras = CursorUtil.getColumnIndexOrThrow(_cursor, "duracionHoras");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfFechaSolicitud = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaSolicitud");
          final int _cursorIndexOfFechaLimiteDevolucion = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaLimiteDevolucion");
          final int _cursorIndexOfEvidenciaUri = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaUri");
          final int _cursorIndexOfEvidenciaEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "evidenciaEstado");
          final int _cursorIndexOfLatitud = CursorUtil.getColumnIndexOrThrow(_cursor, "latitud");
          final int _cursorIndexOfLongitud = CursorUtil.getColumnIndexOrThrow(_cursor, "longitud");
          final int _cursorIndexOfSincronizado = CursorUtil.getColumnIndexOrThrow(_cursor, "sincronizado");
          final List<SolicitudEntity> _result = new ArrayList<SolicitudEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SolicitudEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpEquipoId;
            _tmpEquipoId = _cursor.getInt(_cursorIndexOfEquipoId);
            final String _tmpAmbienteDestino;
            _tmpAmbienteDestino = _cursor.getString(_cursorIndexOfAmbienteDestino);
            final String _tmpProposito;
            _tmpProposito = _cursor.getString(_cursorIndexOfProposito);
            final int _tmpDuracionHoras;
            _tmpDuracionHoras = _cursor.getInt(_cursorIndexOfDuracionHoras);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpFechaSolicitud;
            _tmpFechaSolicitud = _cursor.getLong(_cursorIndexOfFechaSolicitud);
            final long _tmpFechaLimiteDevolucion;
            _tmpFechaLimiteDevolucion = _cursor.getLong(_cursorIndexOfFechaLimiteDevolucion);
            final String _tmpEvidenciaUri;
            if (_cursor.isNull(_cursorIndexOfEvidenciaUri)) {
              _tmpEvidenciaUri = null;
            } else {
              _tmpEvidenciaUri = _cursor.getString(_cursorIndexOfEvidenciaUri);
            }
            final String _tmpEvidenciaEstado;
            _tmpEvidenciaEstado = _cursor.getString(_cursorIndexOfEvidenciaEstado);
            final Double _tmpLatitud;
            if (_cursor.isNull(_cursorIndexOfLatitud)) {
              _tmpLatitud = null;
            } else {
              _tmpLatitud = _cursor.getDouble(_cursorIndexOfLatitud);
            }
            final Double _tmpLongitud;
            if (_cursor.isNull(_cursorIndexOfLongitud)) {
              _tmpLongitud = null;
            } else {
              _tmpLongitud = _cursor.getDouble(_cursorIndexOfLongitud);
            }
            final boolean _tmpSincronizado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfSincronizado);
            _tmpSincronizado = _tmp != 0;
            _item = new SolicitudEntity(_tmpId,_tmpEquipoId,_tmpAmbienteDestino,_tmpProposito,_tmpDuracionHoras,_tmpEstado,_tmpFechaSolicitud,_tmpFechaLimiteDevolucion,_tmpEvidenciaUri,_tmpEvidenciaEstado,_tmpLatitud,_tmpLongitud,_tmpSincronizado);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
