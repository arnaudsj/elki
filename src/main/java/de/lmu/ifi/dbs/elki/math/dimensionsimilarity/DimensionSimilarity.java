package de.lmu.ifi.dbs.elki.math.dimensionsimilarity;

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
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;

/**
 * Interface for computing pairwise dimension similarities, used for arranging
 * dimensions in parallel coordinate plots.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.uses DimensionSimilarityMatrix - - «writes»
 * 
 * @param <V> Object type
 */
public interface DimensionSimilarity<V> extends Parameterizable {
  /**
   * Compute the dimension similarity matrix
   * 
   * @param database Database context
   * @param relation Relation
   * @param subset DBID subset (for sampling / selection)
   * @param matrix Matrix to fill
   */
  public void computeDimensionSimilarites(Database database, Relation<? extends V> relation, DBIDs subset, DimensionSimilarityMatrix matrix);
}
