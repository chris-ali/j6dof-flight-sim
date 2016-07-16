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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import eu.hansolo.steelseries.gauges.AbstractGauge;
import eu.hansolo.steelseries.gauges.AbstractRadial;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.Util;

public final class AirspeedIndicator extends AbstractRadial {

	private static final long serialVersionUID = 1L;

    private double oldValue = 0;
    private double rotationAngle = 0;
    private final Point2D CENTER = new Point2D.Double();
    private BufferedImage frameImage;
    private BufferedImage backgroundImage;
    private BufferedImage lcdImage;
    private BufferedImage tickmarksImage;
    private BufferedImage pointerImage;
    private BufferedImage foregroundImage;
    private BufferedImage disabledImage;
    private Timeline timeline = new Timeline(this);
    private final Rectangle2D LCD = new Rectangle2D.Double();
    private final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);
    private TextLayout unitLayout;
    private final Rectangle2D UNIT_BOUNDARY = new Rectangle2D.Double();
    private double unitStringWidth;
    private TextLayout valueLayout;
    private final Rectangle2D VALUE_BOUNDARY = new Rectangle2D.Double();

    public AirspeedIndicator() {
        super();
        init(getInnerBounds().width, getInnerBounds().height);
        setLcdVisible(false);
        setUnitString("KNOTS");
        setTitle(this.toString());
    }

    @Override
    public final AbstractGauge init(int WIDTH, int HEIGHT) {
        if (WIDTH <= 1 || HEIGHT <= 1) {
            return this;
        }

        if (isDigitalFont()) {
            setLcdValueFont(getModel().getDigitalBaseFont().deriveFont(0.7f * WIDTH * 0.10f));
        } else {
            setLcdValueFont(getModel().getStandardBaseFont().deriveFont(0.625f * WIDTH * 0.10f));
        }

        if (isCustomLcdUnitFontEnabled()) {
            setLcdUnitFont(getCustomLcdUnitFont().deriveFont(0.25f * WIDTH * 0.10f));
        } else {
            setLcdUnitFont(getModel().getStandardBaseFont().deriveFont(0.25f * WIDTH * 0.10f));
        }

        setLcdInfoFont(getModel().getStandardInfoFont().deriveFont(0.15f * WIDTH * 0.10f));

        CENTER.setLocation(getGaugeBounds().getCenterX() - getInsets().left, getGaugeBounds().getCenterY() - getInsets().top);

        if (frameImage != null) {
            frameImage.flush();
        }
        frameImage = FRAME_FACTORY.createRadialFrame(WIDTH, FrameDesign.TILTED_BLACK, getCustomFrameDesign(), getFrameEffect(), backgroundImage);

        if (backgroundImage != null) {
            backgroundImage.flush();
        }
        backgroundImage = create_BACKGROUND_Image(WIDTH);

        if (lcdImage != null) {
            lcdImage.flush();
        }
        
        if (isLcdVisible()) {
        	lcdImage = create_LCD_Image((int) (WIDTH * 0.32), (int) (WIDTH * 0.10), getLcdColor(), getCustomLcdBackground());
        	LCD.setRect(((getGaugeBounds().width - lcdImage.getWidth()) / 2.0), ((getGaugeBounds().height - lcdImage.getHeight()) / 1.6), WIDTH * 0.32, WIDTH * 0.10);
        }
        
        create_TITLE_Image(WIDTH, getTitle(), getUnitString(), backgroundImage);
        
        if (tickmarksImage != null) {
            tickmarksImage.flush();
        }
        tickmarksImage = create_TICKMARKS_Image(WIDTH);

        if (pointerImage != null) {
            pointerImage.flush();
        }
        pointerImage = create_POINTER_Image(WIDTH);

        if (foregroundImage != null) {
            foregroundImage.flush();
        }
        foregroundImage = create_FOREGROUND_Image(WIDTH, false, getForegroundType());

        if (disabledImage != null) {
            disabledImage.flush();
        }
        disabledImage = create_DISABLED_Image(WIDTH);

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isInitialized()) {
            return;
        }

        final Graphics2D G2 = (Graphics2D) g.create();

        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Translate the coordinate system related to insets
        G2.translate(getInnerBounds().x, getInnerBounds().y);

        final AffineTransform OLD_TRANSFORM = G2.getTransform();

        // Draw the frame
        if (isFrameVisible()) {
            G2.drawImage(frameImage, 0, 0, null);
        }

        // Draw the background
        if (isBackgroundVisible()) {
            G2.drawImage(backgroundImage, 0, 0, null);
        }

        // Draw the tickmarks
        G2.drawImage(tickmarksImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);

        // Draw LCD display
        if (isLcdVisible()) {
            G2.drawImage(lcdImage, (int) (LCD.getX()), (int) (LCD.getY()), null);

            if (getLcdColor() == LcdColor.CUSTOM) {
                G2.setColor(getCustomLcdForeground());
            } else {
                G2.setColor(getLcdColor().TEXT_COLOR);
            }
            G2.setFont(getLcdUnitFont());
            if (isLcdUnitStringVisible()) {
                unitLayout = new TextLayout(getLcdUnitString(), G2.getFont(), RENDER_CONTEXT);
                UNIT_BOUNDARY.setFrame(unitLayout.getBounds());
                G2.drawString(getLcdUnitString(), (int) (LCD.getX() + (LCD.getWidth() - UNIT_BOUNDARY.getWidth()) - LCD.getWidth() * 0.03), (int) (LCD.getY() + LCD.getHeight() * 0.76));
                unitStringWidth = UNIT_BOUNDARY.getWidth();
            } else {
                unitStringWidth = 0;
            }
            G2.setFont(getLcdValueFont());
            
            valueLayout = new TextLayout(formatLcdValue(getLcdValue()), G2.getFont(), RENDER_CONTEXT);
            VALUE_BOUNDARY.setFrame(valueLayout.getBounds());
            G2.drawString(formatLcdValue(getLcdValue()), (int) (LCD.getX() + (LCD.getWidth() - unitStringWidth - VALUE_BOUNDARY.getWidth()) - LCD.getWidth() * 0.09), (int) (LCD.getY() + lcdImage.getHeight() * 0.76));
        }
        
        // Draw pointer
        G2.rotate(rotationAngle, CENTER.getX(), CENTER.getY());
        G2.drawImage(pointerImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);

        // Draw foreground
        if (isForegroundVisible()) {
            G2.drawImage(foregroundImage, 0, 0, null);
        }

        if (!isEnabled()) {
            G2.drawImage(disabledImage, 0, 0, null);
        }

        G2.dispose();
    }

    @Override
    public double getValue() {
        return this.oldValue;
    }

    @Override
    public void setValue(final double VALUE) {
    	if (VALUE <= 200) {
    		if (VALUE < 30)
    			rotationAngle = (2.0 * Math.PI / 200) * (VALUE/3); // Change scale so that gauge points up at 0 kts
    		else
    			rotationAngle = (2.0 * Math.PI / 200) * (VALUE-20);
	        this.oldValue = VALUE;
	        if (isValueCoupled()) {
	            setLcdValue(VALUE);
	        }
    	}
        fireStateChanged();
        repaint(getInnerBounds());
    }

    @Override
    public void setValueAnimated(final double VALUE) {
        if (timeline.getState() == Timeline.TimelineState.PLAYING_FORWARD || timeline.getState() == Timeline.TimelineState.PLAYING_REVERSE) {
            timeline.abort();
        }
        timeline = new Timeline(this);
        timeline.addPropertyToInterpolate("value", this.oldValue, VALUE);
        timeline.setEase(new Spline(0.5f));

        timeline.setDuration(1000);
        timeline.play();
    }

    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return 10;
    }

    @Override
    public java.awt.Paint createCustomLcdBackgroundPaint(final Color[] LCD_COLORS) {
        final Point2D FOREGROUND_START = new Point2D.Double(0.0, LCD.getMinY() + 1.0);
        final Point2D FOREGROUND_STOP = new Point2D.Double(0.0, LCD.getMaxY() - 1);
        if (FOREGROUND_START.equals(FOREGROUND_STOP)) {
            FOREGROUND_STOP.setLocation(0.0, FOREGROUND_START.getY() + 1);
        }

        final float[] FOREGROUND_FRACTIONS = {
            0.0f,
            0.03f,
            0.49f,
            0.5f,
            1.0f
        };

        final Color[] FOREGROUND_COLORS = {
            LCD_COLORS[0],
            LCD_COLORS[1],
            LCD_COLORS[2],
            LCD_COLORS[3],
            LCD_COLORS[4]
        };
        Util.INSTANCE.validateGradientPoints(FOREGROUND_START, FOREGROUND_STOP);
        return new LinearGradientPaint(FOREGROUND_START, FOREGROUND_STOP, FOREGROUND_FRACTIONS, FOREGROUND_COLORS);
    }

    @Override
    public Point2D getCenter() {
        return new Point2D.Double(getInnerBounds().getCenterX() + getInnerBounds().x, getInnerBounds().getCenterX() + getInnerBounds().y);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return getInnerBounds();
    }

    @Override
    public Rectangle getLcdBounds() {
        return LCD.getBounds();
    }

    private BufferedImage create_TICKMARKS_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        final int IMAGE_WIDTH = IMAGE.getWidth();

        final AffineTransform OLD_TRANSFORM = G2.getTransform();

        final BasicStroke THIN_STROKE = new BasicStroke(0.01f * IMAGE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final Font FONT = new Font("Verdana", 0, (int) (0.0747663551f * IMAGE_WIDTH));
        final float TEXT_DISTANCE = 0.1f * IMAGE_WIDTH;
        final float MIN_LENGTH = 0.02f * IMAGE_WIDTH;
        final float MED_LENGTH = 0.04f * IMAGE_WIDTH;

        // Create the watch itself
        final float RADIUS = IMAGE_WIDTH * 0.37f;
        CENTER.setLocation(IMAGE_WIDTH / 2.0f, IMAGE_WIDTH / 2.0f);

        // Draw ticks
        Point2D innerPoint = new Point2D.Double();
        Point2D outerPoint = new Point2D.Double();
        Point2D textPoint = new Point2D.Double();
        Line2D tick;
        int tickCounterFull = 2;
        int tickCounterHalf = 0;
        int counter = 0;

        double sinValue = 0;
        double cosValue = 0;

        final double STEP = (2.0 * Math.PI) / (40.0);

        if (isTickmarkColorFromThemeEnabled()) {
            G2.setColor(getBackgroundColor().LABEL_COLOR);
        } else {
            G2.setColor(getTickmarkColor());
        }

        for (double alpha = 2.0 * Math.PI; alpha >= Math.PI/2.75; alpha -= STEP) {
            G2.setStroke(THIN_STROKE);
            sinValue = Math.sin(alpha - 1.2*Math.PI);
            cosValue = Math.cos(alpha - 1.2*Math.PI);

            if (tickCounterHalf == 1) {
                G2.setStroke(THIN_STROKE);
                innerPoint.setLocation(CENTER.getX() + (RADIUS - MIN_LENGTH) * sinValue, CENTER.getY() + (RADIUS - MIN_LENGTH) * cosValue);
                outerPoint.setLocation(CENTER.getX() + RADIUS * sinValue, CENTER.getY() + RADIUS * cosValue);
                // Draw ticks
                tick = new Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);

                tickCounterHalf = 0;
            }

            // Different tickmark every 15 units
            if (tickCounterFull == 2) {
                G2.setStroke(THIN_STROKE);
                innerPoint.setLocation(CENTER.getX() + (RADIUS - MED_LENGTH) * sinValue, CENTER.getY() + (RADIUS - MED_LENGTH) * cosValue);
                outerPoint.setLocation(CENTER.getX() + RADIUS * sinValue, CENTER.getY() + RADIUS * cosValue);

                // Draw ticks
                tick = new Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);

                tickCounterFull = 0;
            }

            // Draw text
            G2.setFont(FONT);
            textPoint.setLocation(CENTER.getX() + (RADIUS - TEXT_DISTANCE) * sinValue, CENTER.getY() + (RADIUS - TEXT_DISTANCE) * cosValue);
            if (counter != 40 && counter % 4 == 0) {
                G2.rotate(Math.toRadians(0), CENTER.getX(), CENTER.getY());
                G2.fill(UTIL.rotateTextAroundCenter(G2, String.valueOf(5*counter+40), (int) textPoint.getX(), (int) textPoint.getY(), 0));
            }

            G2.setTransform(OLD_TRANSFORM);

            tickCounterHalf++;
            tickCounterFull++;

            counter++;
        }

        G2.dispose();

        return IMAGE;
    }

    @Override
    protected BufferedImage create_POINTER_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, (int) (1.0 * WIDTH), Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        final GeneralPath POINTER = new GeneralPath();
        POINTER.setWindingRule(Path2D.WIND_EVEN_ODD);
        POINTER.moveTo(IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.4719626168224299);
        POINTER.curveTo(IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.4719626168224299, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.4672897196261682, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.4672897196261682);
        POINTER.lineTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.20093457943925233);
        POINTER.lineTo(IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.16822429906542055);
        POINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.20093457943925233);
        POINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.4672897196261682);
        POINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.4672897196261682, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.4719626168224299, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.4719626168224299);
        POINTER.curveTo(IMAGE_WIDTH * 0.4719626168224299, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.4672897196261682, IMAGE_HEIGHT * 0.49065420560747663, IMAGE_WIDTH * 0.4672897196261682, IMAGE_HEIGHT * 0.5);
        POINTER.curveTo(IMAGE_WIDTH * 0.4672897196261682, IMAGE_HEIGHT * 0.514018691588785, IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.5280373831775701, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5327102803738317);
        POINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5327102803738317, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5794392523364486, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5887850467289719);
        POINTER.curveTo(IMAGE_WIDTH * 0.48598130841121495, IMAGE_HEIGHT * 0.5934579439252337, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.5981308411214953, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.6074766355140186);
        POINTER.curveTo(IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.616822429906542, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.6261682242990654, IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.6261682242990654);
        POINTER.curveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.6261682242990654, IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.616822429906542, IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.6074766355140186);
        POINTER.curveTo(IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.5981308411214953, IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.5934579439252337, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.5887850467289719);
        POINTER.curveTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.5794392523364486, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.5327102803738317, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5327102803738317);
        POINTER.curveTo(IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.5280373831775701, IMAGE_WIDTH * 0.5327102803738317, IMAGE_HEIGHT * 0.514018691588785, IMAGE_WIDTH * 0.5327102803738317, IMAGE_HEIGHT * 0.5);
        POINTER.curveTo(IMAGE_WIDTH * 0.5327102803738317, IMAGE_HEIGHT * 0.49065420560747663, IMAGE_WIDTH * 0.5280373831775701, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.4719626168224299);
        POINTER.closePath();
        final Point2D POINTER_START = new Point2D.Double(0, POINTER.getBounds2D().getMinY());
        final Point2D POINTER_STOP = new Point2D.Double(0, POINTER.getBounds2D().getMaxY());
        final float[] POINTER_FRACTIONS = {
            0.0f,
            0.59f,
            0.5901f,
            0.60f,
            1.0f
        };
        final Color[] POINTER_COLORS = {
            new Color(255, 255, 255, 255),
            new Color(255, 255, 255, 255),
            new Color(32, 32, 32, 255),
            new Color(32, 32, 32, 255),
            new Color(32, 32, 32, 255)
        };
        final LinearGradientPaint POINTER_GRADIENT = new LinearGradientPaint(POINTER_START, POINTER_STOP, POINTER_FRACTIONS, POINTER_COLORS);
        G2.setPaint(POINTER_GRADIENT);
        G2.fill(POINTER);

        G2.dispose();

        return IMAGE;
    }

    @Override
    public String toString() {
        return "AIRSPEED";
    }
}
