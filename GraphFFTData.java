import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class GraphData extends ApplicationFrame {

    public GraphData(String applicationTitle, String chartTitle) {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Time (1 = 1/n sec)",
                "Transform",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        final XYPlot plot = xylineChart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(0.5f));
        Shape shape = ShapeUtils.createDownTriangle(0.001f);
        renderer.setSeriesShape(0,shape);


        plot.setRenderer(renderer);

        setContentPane(chartPanel);
    }

    private XYDataset createDataset() {
        final XYSeries plot = new XYSeries("Transformed Data");

        try(BufferedReader br = new BufferedReader(new FileReader("PATH-TO-AUDIO.out"))) {
            double x_val = 0.0;
            for(String line; (line = br.readLine()) != null; ) {
                Double val = Double.parseDouble(line);
                //if (x_val <= 100) {
                    plot.add(x_val, val);
                //}

                x_val++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(plot);

        return dataset;
    }

    public static void graph() {
        GraphData chart = new GraphData("",
                "");
        chart.pack();
        chart.setVisible(true);
    }
}

