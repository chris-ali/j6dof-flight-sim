/*
 * Copyright (c) 2012, Gerrit Grunwald
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * The names of its contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.chrisali.javaflightsim.instrumentpanel.gauges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import eu.hansolo.steelseries.gauges.AbstractGauge;
import eu.hansolo.steelseries.gauges.AbstractRadial;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.Section;


public final class TurnCoordinator extends AbstractRadial {

	private static final long serialVersionUID = 1L;

    private double turnRateValue = 0;
    private double coordValue = 0;
    private boolean decimalVisible = false;
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
    private double angleStep;
    private Font font = new Font("Verdana", 0, 18);
    private final Point2D CENTER = new Point2D.Double();
    // Images used to combine layers for background and foreground
    private BufferedImage bImage;
    private BufferedImage fImage;
    private BufferedImage pointerImage;
    private BufferedImage disabledImage;
    private BufferedImage coordinatorTubeImage;
    private BufferedImage coordinatorBallImage;
    private Timeline inclinTimeline = new Timeline(this);
    private Timeline coordTimeline = new Timeline(this);
    private final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);
    private TextLayout textLayout;
    private final Rectangle2D TEXT_BOUNDARY = new Rectangle2D.Double();

    public TurnCoordinator() {
        super();
        setMinValue(-10);
        setMaxValue(10);
        calcAngleStep();
        setEnabled(true);
        setDecimalVisible(false);
        setLcdVisible(false);
        setUnitString("");
		setTitle(this.toString());
        init(getInnerBounds().width, getInnerBounds().height);
    }

    @Override
    public AbstractGauge init(final int WIDTH, final int HEIGHT) {
        final int GAUGE_WIDTH = isFrameVisible() ? WIDTH : getGaugeBounds().width;
        final int GAUGE_HEIGHT = isFrameVisible() ? HEIGHT : getGaugeBounds().height;

        if (isFrameVisible()) {
            setFramelessOffset(0, 0);
        } else {
            setFramelessOffset(getGaugeBounds().width * 0.0841121495, getGaugeBounds().width * 0.0841121495);
        }

        if (GAUGE_WIDTH <= 1 || GAUGE_HEIGHT <= 1) {
            return this;
        }

        // Create Background Image
        if (bImage != null) {
            bImage.flush();
        }
        bImage = UTIL.createImage(GAUGE_WIDTH, GAUGE_WIDTH, java.awt.Transparency.TRANSLUCENT);

        // Create Foreground Image
        if (fImage != null) {
            fImage.flush();
        }
        fImage = UTIL.createImage(GAUGE_WIDTH, GAUGE_WIDTH, java.awt.Transparency.TRANSLUCENT);

        if (isFrameVisible()) {
        	FRAME_FACTORY.createRadialFrame(GAUGE_WIDTH, FrameDesign.TILTED_BLACK, getCustomFrameDesign(), getFrameEffect(), bImage);
        }

        if (isBackgroundVisible()) {
            create_BACKGROUND_Image(GAUGE_WIDTH, "", "", bImage);
        }

        create_TICKMARKS_Image(GAUGE_WIDTH, 0, 0, 0, 0, 0, 0, 0, true, true, null, bImage);
        
        create_TITLE_Image(WIDTH, getTitle(), getUnitString(), bImage);

        if (pointerImage != null) {
            pointerImage.flush();
        }
        pointerImage = create_POINTER_Image(GAUGE_WIDTH);
        
        coordinatorBallImage = create_BALL_Image(GAUGE_WIDTH);
        
        coordinatorTubeImage = create_COORDINATOR_Image(GAUGE_WIDTH);

        if (isForegroundVisible()) {
        	FOREGROUND_FACTORY.createRadialForeground(GAUGE_WIDTH, false, getForegroundType(), fImage);
        }

        if (disabledImage != null) {
            disabledImage.flush();
        }
        disabledImage = create_DISABLED_Image(GAUGE_WIDTH);

        font = new java.awt.Font("Verdana", 0, (int) (0.10 * getWidth()));

        return this;
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        if (!isInitialized()) {
            return;
        }

        final java.awt.Graphics2D G2 = (java.awt.Graphics2D) g.create();

        G2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(java.awt.RenderingHints.KEY_STROKE_CONTROL, java.awt.RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Translate the coordinate system related to insets
        G2.translate(getFramelessOffset().getX()+7, getFramelessOffset().getY()+7);

        CENTER.setLocation(getGaugeBounds().getCenterX() - getInsets().left, getGaugeBounds().getCenterY() - getInsets().top);

        final AffineTransform OLD_TRANSFORM = G2.getTransform();

        // Draw combined background image
        G2.drawImage(bImage, 0, 0, null);

        // Draw text if textorientation is fixed
        G2.translate(getFramelessOffset().getX(), getFramelessOffset().getY());
        G2.setColor(super.getBackgroundColor().LABEL_COLOR);
        if (decimalVisible) {
            G2.setFont(font.deriveFont(0.10f * getInnerBounds().width));
        } else {
            G2.setFont(font.deriveFont(0.15f * getInnerBounds().width));
        }
        
        if (isLcdVisible()) {
        	textLayout = new TextLayout(DECIMAL_FORMAT.format(turnRateValue), G2.getFont(), RENDER_CONTEXT);
        	TEXT_BOUNDARY.setFrame(textLayout.getBounds());
        	G2.drawString(DECIMAL_FORMAT.format(turnRateValue), (int) ((getInnerBounds().width - TEXT_BOUNDARY.getWidth()) / 2.0), (int) ((getInnerBounds().width - TEXT_BOUNDARY.getHeight()) / 2.0) + textLayout.getAscent() - textLayout.getDescent());
        	G2.translate(-getFramelessOffset().getX(), -getFramelessOffset().getY());
        }
        
        // Draw Inclinometer and Ball
        G2.drawImage(coordinatorBallImage, (int) coordValue, 0, null);
        G2.translate(coordValue, 0);
        
        G2.setTransform(OLD_TRANSFORM);
        
        G2.drawImage(coordinatorTubeImage, 0, 0, null);
        
        // Draw Pointer
        G2.rotate(Math.toRadians(turnRateValue*angleStep), CENTER.getX(), CENTER.getY());
        G2.drawImage(pointerImage, 0, 0, null);

        G2.setTransform(OLD_TRANSFORM);
              
        // Draw combined foreground image
        G2.drawImage(fImage, 0, 0, null);

        if (!isEnabled()) {
            G2.drawImage(disabledImage, 0, 0, null);
        }

        // Translate the coordinate system back to original
        G2.translate(-getInnerBounds().x, -getInnerBounds().y);

        G2.dispose();
    }

    /**
     * Sets the current inclinometer value 
     * @param VALUE
     */
    public void setInclinoValue(final double VALUE) {
        if (isEnabled()) {
        	if (Math.abs(VALUE) <= 10) {
        		this.turnRateValue = VALUE;
        	}
            fireStateChanged();
            repaint();
        }
    }

    public void setInclinoValueAnimated(double value) {
        if (isEnabled()) {
            if (inclinTimeline.getState() == Timeline.TimelineState.PLAYING_FORWARD || inclinTimeline.getState() == Timeline.TimelineState.PLAYING_REVERSE) {
                inclinTimeline.abort();
            }
            inclinTimeline = new Timeline(this);
            inclinTimeline.addPropertyToInterpolate("value", getValue(), value);
            inclinTimeline.setEase(new Spline(0.5f));

            inclinTimeline.setDuration(1500);
            inclinTimeline.play();
        }
    }
    
    /**
     * Sets the current ball value 
     * @param VALUE
     */
    public void setCoordValue(final double VALUE) {
        if (isEnabled()) {
        	if (Math.abs(VALUE) <= 12.5) {
        		this.coordValue = VALUE*12.5;
        	}
            fireStateChanged();
            repaint();
        }
    }

    public void setCoordValueAnimated(double value) {
        if (isEnabled()) {
            if (coordTimeline.getState() == Timeline.TimelineState.PLAYING_FORWARD || coordTimeline.getState() == Timeline.TimelineState.PLAYING_REVERSE) {
            	coordTimeline.abort();
            }
            coordTimeline = new Timeline(this);
            coordTimeline.addPropertyToInterpolate("value", getValue(), value);
            coordTimeline.setEase(new Spline(0.5f));

            coordTimeline.setDuration(250);
            coordTimeline.play();
        }
    }

    @Override
    public double getMinValue() {
        return -10.0;
    }

    @Override
    public double getMaxValue() {
        return 10.0;
    }

    /**
     * Returns true if decimals will be shown on the degree value
     * @return true if decimals will be shown on the degree value
     */
    public boolean isDecimalVisible() {
        return this.decimalVisible;
    }

    /**
     * Enables / disables the visibility of the decimals on the degree value
     * @param DECIMAL_VISIBLE
     */
    public void setDecimalVisible(final boolean DECIMAL_VISIBLE) {
        if (DECIMAL_VISIBLE) {
            DECIMAL_FORMAT.applyPattern("0.0");
        } else {
            DECIMAL_FORMAT.applyPattern("0");
        }
        this.decimalVisible = DECIMAL_VISIBLE;
        repaint();
    }

    private void calcAngleStep() {
        angleStep = (45.0 * Math.PI) / (getMaxValue() - getMinValue());
    }

    @Override
    public Point2D getCenter() {
        return new java.awt.geom.Point2D.Double(bImage.getWidth() / 2.0 + getInnerBounds().x, bImage.getHeight() / 2.0 + getInnerBounds().y);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new java.awt.geom.Rectangle2D.Double(bImage.getMinX(), bImage.getMinY(), bImage.getWidth(), bImage.getHeight());
    }

    @Override
    public Rectangle getLcdBounds() {
        return new Rectangle();
    }

    private BufferedImage create_TICKMARKS_Image(final int WIDTH, final double FREE_AREA_ANGLE,
                                                                final double OFFSET, final double MIN_VALUE,
                                                                final double MAX_VALUE, final double ANGLE_STEP,
                                                                final int TICK_LABEL_PERIOD,
                                                                final int SCALE_DIVIDER_POWER, final boolean DRAW_TICKS,
                                                                final boolean DRAW_TICK_LABELS,
                                                                ArrayList<Section> tickmarkSections,
                                                                BufferedImage image) {
        if (WIDTH <= 0) {
            return null;
        }
        if (image == null) {
            image = UTIL.createImage(WIDTH, (int) (1.0 * WIDTH), Transparency.TRANSLUCENT);
        }
        final Graphics2D G2 = image.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final int IMAGE_WIDTH = image.getWidth();
        final int IMAGE_HEIGHT = image.getHeight();

        final Font STD_FONT = new Font("Verdana", 0, (int) (0.075 * WIDTH));
        final Font SMALL_FONT = new Font("Verdana", 0, (int) (0.0375 * WIDTH));
        
        final BasicStroke THICK_STROKE = new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final int TEXT_DISTANCE = (int) (0.08 * WIDTH);
        final int MIN_LENGTH = (int) (0.0133333333 * WIDTH);
        final int MAX_LENGTH = (int) (0.04 * WIDTH);

        // Create the ticks itself
        final float RADIUS = IMAGE_WIDTH * 0.38f;
        final Point2D GAUGE_CENTER = new Point2D.Double(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT / 2.0f);

        // Draw ticks
        Point2D innerPoint;
        Point2D outerPoint;
        Point2D textPoint = null;
        Line2D tick;
        int counter = 0;

        G2.setFont(STD_FONT);

        double sinValue = 0;
        double cosValue = 0;

        final double STEP = (2.0 * Math.PI) / (360.0);

        for (double alpha = (2.0 * Math.PI); alpha >= STEP; alpha -= STEP) {
            sinValue = Math.sin(alpha);
            cosValue = Math.cos(alpha);
            textPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - TEXT_DISTANCE) * cosValue);
            innerPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - MIN_LENGTH) * sinValue, GAUGE_CENTER.getY() + (RADIUS - MIN_LENGTH) * cosValue);
            outerPoint = new Point2D.Double(GAUGE_CENTER.getX() + RADIUS * sinValue, GAUGE_CENTER.getY() + RADIUS * cosValue);
            G2.setColor(super.getBackgroundColor().LABEL_COLOR);

            if (counter == 90 || counter == 270 || counter == 70 || counter == 290) {
                G2.setColor(super.getBackgroundColor().LABEL_COLOR);
                G2.setStroke(THICK_STROKE);
                innerPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - MAX_LENGTH) * sinValue, GAUGE_CENTER.getY() + (RADIUS - MAX_LENGTH) * cosValue);
                outerPoint = new Point2D.Double(GAUGE_CENTER.getX() + RADIUS * sinValue, GAUGE_CENTER.getY() + RADIUS * cosValue);

                // Draw ticks
                tick = new Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);
            }
            else if (counter == 50) {
            	// Draw outer text
                textPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - 0.25*TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - 0.25*TEXT_DISTANCE) * cosValue);
                G2.setFont(STD_FONT);

                G2.fill(UTIL.rotateTextAroundCenter(G2, "L", (int) textPoint.getX(), (int) textPoint.getY(), 0));
            }
            else if (counter == 310) {
            	// Draw outer text
                textPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - 0.25*TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - 0.25*TEXT_DISTANCE) * cosValue);
                G2.setFont(STD_FONT);

                G2.fill(UTIL.rotateTextAroundCenter(G2, "R", (int) textPoint.getX(), (int) textPoint.getY(), 0));
            }
            else if (counter == 359) {
            	// Draw outer text
            	textPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - 0.25*TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - 1.05*TEXT_DISTANCE) * cosValue);
                G2.setFont(STD_FONT);

                G2.fill(UTIL.rotateTextAroundCenter(G2, "2 MIN", (int) textPoint.getX(), (int) textPoint.getY(), 0));
            	
                textPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - 0.25*TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - 0.6*TEXT_DISTANCE) * cosValue);
                G2.setFont(SMALL_FONT);

                G2.fill(UTIL.rotateTextAroundCenter(G2, "NO PITCH", (int) textPoint.getX(), (int) textPoint.getY(), 0));
                
                textPoint = new Point2D.Double(GAUGE_CENTER.getX() + (RADIUS - 0.25*TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - 0.15*TEXT_DISTANCE) * cosValue);
                G2.setFont(SMALL_FONT);

                G2.fill(UTIL.rotateTextAroundCenter(G2, "INFORMATION", (int) textPoint.getX(), (int) textPoint.getY(), 0));
            }

            counter++;

        }

        G2.dispose();

        return image;
    }
    
    private BufferedImage create_COORDINATOR_Image(final int WIDTH) {
    	if (WIDTH <= 0) {
            return null;
        }
    	
    	final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();
        
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        final GeneralPath COORDINATOR = new GeneralPath();
        COORDINATOR.setWindingRule(Path2D.WIND_EVEN_ODD);
        
        COORDINATOR.moveTo(IMAGE_WIDTH * 0.28, IMAGE_HEIGHT * 0.725);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.72, IMAGE_HEIGHT * 0.725);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.75, IMAGE_HEIGHT * 0.60);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.25, IMAGE_HEIGHT * 0.60);
        COORDINATOR.closePath();
        
        // Left center line
        COORDINATOR.moveTo(IMAGE_WIDTH * 0.445, IMAGE_HEIGHT * 0.725);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.455, IMAGE_HEIGHT * 0.725);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.455, IMAGE_HEIGHT * 0.60);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.445, IMAGE_HEIGHT * 0.60);
        COORDINATOR.closePath();
        
        // Right center line
        COORDINATOR.moveTo(IMAGE_WIDTH * 0.545, IMAGE_HEIGHT * 0.725);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.555, IMAGE_HEIGHT * 0.725);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.555, IMAGE_HEIGHT * 0.60);
        COORDINATOR.lineTo(IMAGE_WIDTH * 0.545, IMAGE_HEIGHT * 0.60);
        COORDINATOR.closePath();
        
        final Point2D COORDINATOR_START = new Point2D.Double(0, COORDINATOR.getBounds2D().getMinY());
        final Point2D COORDINATOR_STOP = new Point2D.Double(0, COORDINATOR.getBounds2D().getMaxY());
        final float[] COORDINATOR_FRACTIONS = {
            0.0f,
            0.33f,
            0.66f,
            1.0f
        };
        
        final Color[] COORDINATOR_COLORS = {
            UTIL.setAlpha(getLabelColor(), 100),
            UTIL.setAlpha(getLabelColor(), 140),
            UTIL.setAlpha(getLabelColor(), 180),
            UTIL.setAlpha(getLabelColor(), 220)
        };
        final LinearGradientPaint COORDINATOR_GRADIENT = new LinearGradientPaint(COORDINATOR_START, COORDINATOR_STOP, COORDINATOR_FRACTIONS, COORDINATOR_COLORS);
        G2.setPaint(COORDINATOR_GRADIENT);
        G2.fill(COORDINATOR);
        G2.setColor(getBackgroundColor().LABEL_COLOR);
        G2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        G2.draw(COORDINATOR);

        G2.dispose();

        return IMAGE;
    }
    
    private BufferedImage create_BALL_Image(final int WIDTH) {
    	if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();
        
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        final Ellipse2D POINTER = new Ellipse2D.Double(IMAGE_WIDTH*0.46, IMAGE_HEIGHT*0.62, IMAGE_WIDTH*0.08, IMAGE_HEIGHT*0.08);
        
        final Point2D BALL_START = new Point2D.Double(POINTER.getBounds2D().getMinX(), 0);
        final Point2D BALL_STOP = new Point2D.Double(POINTER.getBounds2D().getMaxX(), 0);
        final float[] BALL_FRACTIONS = {
                0.0f,
                0.3f,
                0.59f,
                1.0f
            };
            
            final Color[] BALL_COLORS = {
                UTIL.setAlpha(Color.BLACK, 100),
                UTIL.setAlpha(Color.BLACK, 140),
                UTIL.setAlpha(Color.BLACK, 180),
                UTIL.setAlpha(Color.BLACK, 220)
            };
            final LinearGradientPaint BALL_GRADIENT = new LinearGradientPaint(BALL_START, BALL_STOP, BALL_FRACTIONS, BALL_COLORS);
            G2.setPaint(BALL_GRADIENT);
            G2.fill(POINTER);
            G2.setColor(getBackgroundColor().LABEL_COLOR);
            G2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            G2.draw(POINTER);

            G2.dispose();

            return IMAGE;
    }
    
    @Override
    protected BufferedImage create_POINTER_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();
        
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        final GeneralPath POINTER = new GeneralPath();
        POINTER.setWindingRule(Path2D.WIND_EVEN_ODD);
        
        POINTER.moveTo(IMAGE_WIDTH * 0.18, IMAGE_HEIGHT * 0.51);
        POINTER.lineTo(IMAGE_WIDTH * 0.82, IMAGE_HEIGHT * 0.51);
        POINTER.lineTo(IMAGE_WIDTH * 0.82, IMAGE_HEIGHT * 0.50);
        POINTER.lineTo(IMAGE_WIDTH * 0.18, IMAGE_HEIGHT * 0.50);
        POINTER.closePath();
        
        POINTER.moveTo(IMAGE_WIDTH * 0.495, IMAGE_HEIGHT * 0.44);
        POINTER.lineTo(IMAGE_WIDTH * 0.505, IMAGE_HEIGHT * 0.44);
        POINTER.lineTo(IMAGE_WIDTH * 0.505, IMAGE_HEIGHT * 0.50);
        POINTER.lineTo(IMAGE_WIDTH * 0.495, IMAGE_HEIGHT * 0.50);
        POINTER.closePath();
        
        POINTER.moveTo(IMAGE_WIDTH * 0.40, IMAGE_HEIGHT * 0.53);
        POINTER.lineTo(IMAGE_WIDTH * 0.47, IMAGE_HEIGHT * 0.53);
        POINTER.lineTo(IMAGE_WIDTH * 0.47, IMAGE_HEIGHT * 0.54);
        POINTER.lineTo(IMAGE_WIDTH * 0.40, IMAGE_HEIGHT * 0.54);
        POINTER.closePath();
        
        POINTER.moveTo(IMAGE_WIDTH * 0.53, IMAGE_HEIGHT * 0.53);
        POINTER.lineTo(IMAGE_WIDTH * 0.60, IMAGE_HEIGHT * 0.53);
        POINTER.lineTo(IMAGE_WIDTH * 0.60, IMAGE_HEIGHT * 0.54);
        POINTER.lineTo(IMAGE_WIDTH * 0.53, IMAGE_HEIGHT * 0.54);
        POINTER.closePath();
        
        POINTER.moveTo(IMAGE_WIDTH * 0.47, IMAGE_HEIGHT * 0.51);
        POINTER.lineTo(IMAGE_WIDTH * 0.53, IMAGE_HEIGHT * 0.51);
        POINTER.lineTo(IMAGE_WIDTH * 0.53, IMAGE_HEIGHT * 0.56);
        POINTER.lineTo(IMAGE_WIDTH * 0.47, IMAGE_HEIGHT * 0.56);
        POINTER.closePath();
                
        final Point2D POINTER_START = new Point2D.Double(POINTER.getBounds2D().getMinX(), 0);
        final Point2D POINTER_STOP = new Point2D.Double(POINTER.getBounds2D().getMaxX(), 0);
        final float[] POINTER_FRACTIONS = {
            0.0f,
            0.3f,
            0.59f,
            1.0f
        };
        
        final Color[] POINTER_COLORS = {
            UTIL.setAlpha(getLabelColor(), 240),
            UTIL.setAlpha(getLabelColor(), 240),
            UTIL.setAlpha(getLabelColor(), 240),
            UTIL.setAlpha(getLabelColor(), 240)
        };
        final LinearGradientPaint POINTER_GRADIENT = new LinearGradientPaint(POINTER_START, POINTER_STOP, POINTER_FRACTIONS, POINTER_COLORS);
        G2.setPaint(POINTER_GRADIENT);
        G2.fill(POINTER);
        G2.setColor(getBackgroundColor().LABEL_COLOR);
        G2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        G2.draw(POINTER);

        G2.dispose();

        return IMAGE;
    }

    @Override
    public String toString() {
        return "TURN COORDINATOR";
    }
}