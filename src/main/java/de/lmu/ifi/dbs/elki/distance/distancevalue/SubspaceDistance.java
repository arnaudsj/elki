package de.lmu.ifi.dbs.elki.distance.distancevalue;

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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.regex.Pattern;

import de.lmu.ifi.dbs.elki.utilities.FormatUtil;

/**
 * The subspace distance is a special distance that indicates the dissimilarity
 * between subspaces of equal dimensionality. The subspace distance between two
 * points is a pair consisting of the distance between the two subspaces spanned
 * by the strong eigenvectors of the two points and the affine distance between
 * the two subspaces.
 * 
 * @author Elke Achtert
 */
public class SubspaceDistance extends AbstractDistance<SubspaceDistance> {
  /**
   * The static factory instance
   */
  public static final SubspaceDistance FACTORY = new SubspaceDistance();

  /**
   * Serial version number.
   */
  private static final long serialVersionUID = 1;

  /**
   * Indicates a separator.
   */
  public static final String SEPARATOR = "x";

  /**
   * The pattern for parsing subspace values
   */
  public static final Pattern SUBSPACE_PATTERN = Pattern.compile("\\d+(\\.\\d+)?([eE][-]?\\d+)?" + Pattern.quote(SEPARATOR) + "\\d+(\\.\\d+)?([eE][-]?\\d+)?");

  /**
   * The subspace distance.
   */
  private double subspaceDistance;

  /**
   * The affine distance.
   */
  private double affineDistance;

  /**
   * Empty constructor for serialization purposes.
   */
  public SubspaceDistance() {
    // for serialization
  }

  /**
   * Constructs a new SubspaceDistance object consisting of the specified
   * subspace distance and affine distance.
   * 
   * @param subspaceDistance the distance between the two subspaces spanned by
   *        the strong eigenvectors of the two points
   * @param affineDistance the affine distance between the two subspaces
   */
  public SubspaceDistance(double subspaceDistance, double affineDistance) {
    this.subspaceDistance = subspaceDistance;
    this.affineDistance = affineDistance;
  }

  /**
   * Returns a string representation of this SubspaceDistance.
   * 
   * @return the values of the subspace distance and the affine distance
   *         separated by blank
   */
  @Override
  public String toString() {
    return Double.toString(subspaceDistance) + SEPARATOR + Double.toString(affineDistance);
  }

  /**
   * Compares this SubspaceDistance with the given SubspaceDistance wrt the
   * represented subspace distance values. If both values are considered to be
   * equal, the values of the affine distances are compared.
   * 
   * @return the value of {@link Double#compare(double,double)
   *         Double.compare(this.subspaceDistance, other.subspaceDistance)} if
   *         it is a non zero value, the value of
   *         {@link Double#compare(double,double)
   *         Double.compare(this.affineDistance, other.affineDistance)}
   *         otherwise
   */
  @Override
  public int compareTo(SubspaceDistance other) {
    int compare = Double.compare(this.subspaceDistance, other.subspaceDistance);
    if (compare != 0) {
      return compare;
    } else {
      return Double.compare(this.affineDistance, other.affineDistance);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(subspaceDistance);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(affineDistance);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SubspaceDistance other = (SubspaceDistance) obj;
    if (Double.doubleToLongBits(affineDistance) != Double.doubleToLongBits(other.affineDistance)) {
      return false;
    }
    if (Double.doubleToLongBits(subspaceDistance) != Double.doubleToLongBits(other.subspaceDistance)) {
      return false;
    }
    return true;
  }

  /**
   * Returns the value of the subspace distance.
   * 
   * @return the value of the subspace distance
   */
  public double getSubspaceDistance() {
    return subspaceDistance;
  }

  /**
   * Returns the value of the affine distance.
   * 
   * @return the value of the affine distance
   */
  public double getAffineDistance() {
    return affineDistance;
  }

  /**
   * Writes the subspace distance value and the affine distance value of this
   * SubspaceDistance to the specified stream.
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeDouble(subspaceDistance);
    out.writeDouble(affineDistance);
  }

  /**
   * Reads the subspace distance value and the affine distance value of this
   * SubspaceDistance from the specified stream.
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException {
    subspaceDistance = in.readDouble();
    affineDistance = in.readDouble();
  }

  /**
   * Returns the number of Bytes this distance uses if it is written to an
   * external file.
   * 
   * @return 16 (2 * 8 Byte for two double values)
   */
  @Override
  public int externalizableSize() {
    return 16;
  }

  @Override
  public Pattern getPattern() {
    return SUBSPACE_PATTERN;
  }

  @Override
  public SubspaceDistance parseString(String val) throws IllegalArgumentException {
    if (val.equals(INFINITY_PATTERN)) {
      return infiniteDistance();
    }
    if (testInputPattern(val)) {
      String[] values = SEPARATOR.split(val);
      return new SubspaceDistance(FormatUtil.parseDouble(values[0]), FormatUtil.parseDouble(values[1]));
    } else {
      throw new IllegalArgumentException("Given pattern \"" + val + "\" does not match required pattern \"" + requiredInputPattern() + "\"");
    }
  }

  @Override
  public SubspaceDistance infiniteDistance() {
    return new SubspaceDistance(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  }

  @Override
  public SubspaceDistance nullDistance() {
    return new SubspaceDistance(0, 0);
  }

  @Override
  public SubspaceDistance undefinedDistance() {
    return new SubspaceDistance(Double.NaN, Double.NaN);
  }
}
