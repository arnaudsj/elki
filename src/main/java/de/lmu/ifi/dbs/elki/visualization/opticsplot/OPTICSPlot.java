package de.lmu.ifi.dbs.elki.visualization.opticsplot;

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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.List;

import de.lmu.ifi.dbs.elki.distance.distancevalue.CorrelationDistance;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.math.DoubleMinMax;
import de.lmu.ifi.dbs.elki.math.scales.LinearScale;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.result.optics.ClusterOrderEntry;
import de.lmu.ifi.dbs.elki.result.optics.ClusterOrderResult;
import de.lmu.ifi.dbs.elki.visualization.VisualizerContext;
import de.lmu.ifi.dbs.elki.visualization.batikutil.ThumbnailRegistryEntry;
import de.lmu.ifi.dbs.elki.visualization.style.StylingPolicy;

/**
 * Class to produce an OPTICS plot image.
 * 
 * @author Erich Schubert
 * 
 * @apiviz.composedOf LinearScale
 * @apiviz.composedOf OPTICSColorAdapter
 * @apiviz.composedOf OPTICSDistanceAdapter
 * @apiviz.has ClusterOrderResult oneway - - renders
 * 
 * @param <D> Distance type
 */
public class OPTICSPlot<D extends Distance<D>> implements Result {
  /**
   * Logger
   */
  private static final Logging LOG = Logging.getLogger(OPTICSPlot.class);

  /**
   * Scale to use
   */
  LinearScale scale;

  /**
   * Width of plot
   */
  int width;

  /**
   * Height of plot
   */
  int height;

  /**
   * The result to plot
   */
  final ClusterOrderResult<D> co;

  /**
   * Color adapter to use
   */
  final OPTICSColorAdapter colors;

  /**
   * The mapping from cluster order entry to value
   */
  final OPTICSDistanceAdapter<D> distanceAdapter;

  /**
   * The Optics plot.
   */
  protected RenderedImage plot;

  /**
   * The plot number for Batik
   */
  protected int plotnum = -1;

  /**
   * Constructor.
   * 
   * @param co Cluster order to plot.
   * @param colors Coloring strategy
   * @param distanceAdapter Distance adapter
   */
  public OPTICSPlot(ClusterOrderResult<D> co, OPTICSColorAdapter colors, OPTICSDistanceAdapter<D> distanceAdapter) {
    super();
    this.co = co;
    this.colors = colors;
    this.distanceAdapter = distanceAdapter;
  }

  /**
   * Constructor, with automatic distance adapter detection.
   * 
   * @param co Cluster order to plot.
   * @param colors Coloring strategy
   */
  public OPTICSPlot(ClusterOrderResult<D> co, OPTICSColorAdapter colors) {
    super();
    this.co = co;
    this.colors = colors;
    this.distanceAdapter = getAdapterForDistance(co);
  }

  /**
   * Try to find a distance adapter.
   * 
   * @param <D> distance type
   * @param co ClusterOrderResult
   * @return distance adapter
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static <D extends Distance<D>> OPTICSDistanceAdapter<D> getAdapterForDistance(ClusterOrderResult<D> co) {
    Class<?> dcls = co.getDistanceClass();
    if(dcls != null && NumberDistance.class.isAssignableFrom(dcls)) {
      return new OPTICSNumberDistance();
    }
    else if(dcls != null && CorrelationDistance.class.isAssignableFrom(dcls)) {
      return new OPTICSCorrelationDimensionalityDistance();
    }
    else if(dcls == null) {
      throw new UnsupportedOperationException("No distance in cluster order?!?");
    }
    else {
      throw new UnsupportedOperationException("No distance adapter found for distance class: " + dcls);
    }
  }

  /**
   * Test whether this class can produce an OPTICS plot for the given cluster
   * order.
   * 
   * @param <D> Distance type
   * @param co Cluster order result
   * @return test result
   */
  public static <D extends Distance<D>> boolean canPlot(ClusterOrderResult<D> co) {
    try {
      if(getAdapterForDistance(co) != null) {
        return true;
      }
      return false;
    }
    catch(UnsupportedOperationException e) {
      return false;
    }
  }

  /**
   * Trigger a redraw of the OPTICS plot
   */
  public void replot() {
    List<ClusterOrderEntry<D>> order = co.getClusterOrder();

    width = order.size();
    height = Math.min(200, (int) Math.ceil(width / 5));
    if(scale == null) {
      scale = computeScale(order);
    }

    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    int x = 0;
    for(ClusterOrderEntry<D> coe : order) {
      double reach = distanceAdapter.getDoubleForEntry(coe);
      final int y;
      if(!Double.isInfinite(reach) && !Double.isNaN(reach)) {
        y = (height - 1) - (int) Math.floor(scale.getScaled(reach) * (height - 1));
      }
      else {
        y = 0;
      }
      try {
        int col = colors.getColorForEntry(coe);
        for(int y2 = height - 1; y2 >= y; y2--) {
          img.setRGB(x, y2, col);
        }
      }
      catch(ArrayIndexOutOfBoundsException e) {
        LOG.error("Plotting out of range: " + x + "," + y + " >= " + width + "x" + height);
      }
      x++;
    }

    plot = img;
  }

  /**
   * Compute the scale (value range)
   * 
   * @param order Cluster order to process
   * @return Scale for value range of cluster order
   */
  protected LinearScale computeScale(List<ClusterOrderEntry<D>> order) {
    DoubleMinMax range = new DoubleMinMax();
    // calculate range
    for(ClusterOrderEntry<D> coe : order) {
      double reach = distanceAdapter.getDoubleForEntry(coe);
      if(!distanceAdapter.isInfinite(coe) && !Double.isNaN(reach)) {
        range.put(reach);
      }
    }
    // Ensure we have a valid range
    if(!range.isValid()) {
      range.put(0.0);
      range.put(1.0);
    }
    return new LinearScale(range.getMin(), range.getMax());
  }

  /**
   * @return the scale
   */
  public LinearScale getScale() {
    if(plot == null) {
      replot();
    }
    return scale;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    if(plot == null) {
      replot();
    }
    return width;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    if(plot == null) {
      replot();
    }
    return height;
  }

  /**
   * Get width-to-height ratio of image.
   * 
   * @return {@code width / height}
   */
  public double getRatio() {
    if(plot == null) {
      replot();
    }
    return ((double) width) / height;
  }

  /**
   * Get the OPTICS plot.
   * 
   * @return plot image
   */
  public synchronized RenderedImage getPlot() {
    if(plot == null) {
      replot();
    }
    return plot;
  }

  /**
   * Get the distance adapter-
   * 
   * @return the distanceAdapter
   */
  public OPTICSDistanceAdapter<D> getDistanceAdapter() {
    return distanceAdapter;
  }

  /**
   * Free memory used by rendered image.
   */
  public void forgetRenderedImage() {
    plotnum = -1;
    plot = null;
  }

  /**
   * Get the SVG registered plot number
   * 
   * @return Plot URI
   */
  public String getSVGPlotURI() {
    if(plotnum < 0) {
      plotnum = ThumbnailRegistryEntry.registerImage(plot);
    }
    return ThumbnailRegistryEntry.INTERNAL_PREFIX + plotnum;
  }

  @Override
  public String getLongName() {
    return "OPTICS Plot";
  }

  @Override
  public String getShortName() {
    return "optics plot";
  }

  /**
   * Static method to find an optics plot for a result, or to create a new one
   * using the given context.
   * 
   * @param <D> Distance type
   * @param co Cluster order
   * @param context Context (for colors and reference clustering)
   * 
   * @return New or existing optics plot
   */
  public static <D extends Distance<D>> OPTICSPlot<D> plotForClusterOrder(ClusterOrderResult<D> co, VisualizerContext context) {
    // Check for an existing plot
    // ArrayList<OPTICSPlot<D>> plots = ResultUtil.filterResults(co,
    // OPTICSPlot.class);
    // if (plots.size() > 0) {
    // return plots.get(0);
    // }
    // Supported by this class?
    if(!OPTICSPlot.canPlot(co)) {
      return null;
    }
    final StylingPolicy policy = context.getStyleResult().getStylingPolicy();
    final OPTICSColorAdapter opcolor = new OPTICSColorFromStylingPolicy(policy);

    OPTICSPlot<D> opticsplot = new OPTICSPlot<>(co, opcolor);
    // co.addChildResult(opticsplot);
    return opticsplot;
  }

  /**
   * Get the cluster order we are attached to.
   * 
   * @return Cluster order
   */
  public ClusterOrderResult<D> getClusterOrder() {
    return co;
  }
}