package de.lmu.ifi.dbs.elki.database.datastore;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2013
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.distance.distancevalue.DoubleDistance;

/**
 * Storage utility class. Mostly a shorthand for
 * {@link DataStoreFactory#FACTORY}.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.landmark
 * @apiviz.composedOf de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory
 */
public final class DataStoreUtil {
  /**
   * Make a new storage, to associate the given ids with an object of class
   * dataclass.
   * 
   * @param <T> stored data type
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @param dataclass class to store
   * @return new data store
   */
  public static <T> WritableDataStore<T> makeStorage(DBIDs ids, int hints, Class<? super T> dataclass) {
    return DataStoreFactory.FACTORY.makeStorage(ids, hints, dataclass);
  }

  /**
   * Make a new storage, to associate the given ids with an object of class
   * dataclass.
   * 
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @return new data store
   */
  public static WritableDBIDDataStore makeDBIDStorage(DBIDs ids, int hints) {
    return DataStoreFactory.FACTORY.makeDBIDStorage(ids, hints);
  }

  /**
   * Make a new storage, to associate the given ids with an object of class
   * dataclass.
   * 
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @return new data store
   */
  public static WritableDoubleDataStore makeDoubleStorage(DBIDs ids, int hints) {
    return DataStoreFactory.FACTORY.makeDoubleStorage(ids, hints);
  }

  /**
   * Make a new storage, to associate the given ids with an object of class
   * dataclass.
   * 
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @param def Default value
   * @return new data store
   */
  public static WritableDoubleDataStore makeDoubleStorage(DBIDs ids, int hints, double def) {
    return DataStoreFactory.FACTORY.makeDoubleStorage(ids, hints, def);
  }

  /**
   * Make a new storage, to associate the given ids with an object of class
   * dataclass.
   * 
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @return new data store
   */
  public static WritableIntegerDataStore makeIntegerStorage(DBIDs ids, int hints) {
    return DataStoreFactory.FACTORY.makeIntegerStorage(ids, hints);
  }

  /**
   * Make a new storage, to associate the given ids with an object of class
   * dataclass.
   * 
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @param def Default value
   * @return new data store
   */
  public static WritableIntegerDataStore makeIntegerStorage(DBIDs ids, int hints, int def) {
    return DataStoreFactory.FACTORY.makeIntegerStorage(ids, hints, def);
  }

  /**
   * Make a new record storage, to associate the given ids with an object of
   * class dataclass.
   * 
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @param dataclasses classes to store
   * @return new record store
   */
  public static WritableRecordStore makeRecordStorage(DBIDs ids, int hints, Class<?>... dataclasses) {
    return DataStoreFactory.FACTORY.makeRecordStorage(ids, hints, dataclasses);
  }

  /**
   * Make a new storage, to associate the given ids with an double valued
   * distance.
   * 
   * @param ids DBIDs to store data for
   * @param hints Hints for the storage manager
   * @return new data store
   */
  public static WritableDoubleDistanceDataStore makeDoubleDistanceStorage(DBIDs ids, int hints) {
    // FIXME: this is an ugly work-around.
    return (WritableDoubleDistanceDataStore) DataStoreFactory.FACTORY.makeStorage(ids, hints, DoubleDistance.class);
  }
}