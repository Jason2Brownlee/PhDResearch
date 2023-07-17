/*
Optimization Algorithm Toolkit (OAT)
http://sourceforge.net/projects/optalgtoolkit
Copyright (C) 2006-2008  Jason Brownlee

OAT is free software; you can redistribute it and/or modify it under the terms
of the GNU Lesser General Public License as published by the Free Software 
Foundation; either version 3 of the License, or (at your option) any 
later version.

OAT is distributed in the hope that it will be useful, but WITHOUT ANY 
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for 
more details.

You should have received a copy of the GNU Lesser General Public License 
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Jason Brownlee
Project Lead
*/
package com.oat.experimenter;

import java.awt.Dimension;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;

import org.jfree.chart.ChartUtilities;

import com.oat.RunProbe;
import com.oat.experimenter.gui.plots.BoxPlot;
import com.oat.experimenter.stats.AnalysisException;
import com.oat.experimenter.stats.RunStatisticSummary;
import com.oat.experimenter.stats.analysis.StatisticalComparisonTest;
import com.oat.utils.FileUtils;

/**
 * Description: Provides some base functionality for doing analysis on clonal selection studies 
 *  
 * Date: 29/01/2008<br/>
 * @author Jason Brownlee 
 *
 * <br/>
 * <pre>
 * Change History
 * ----------------------------------------------------------------------------
 * 
 * </pre>
 */
public abstract class ClonalSelectionTemplateExperiment extends TemplateExperiment
{
	/**
	 * 
	 * @param exp
	 * @param runMatrix
	 * @param results
	 * @param statistics
	 */
	public void generateCharts(
			Experiment exp, 
			ExperimentalRun [][] runMatrix, 
			RunResult [][][] results, 
			LinkedList<RunProbe> statistics)
	{
		Dimension size = getChartSize();	
		// pre-calculate all summary statistics
		RunStatisticSummary [][][] summaries = new RunStatisticSummary[statistics.size()][][];
		for (int i = 0; i < summaries.length; i++)
		{
			summaries[i] = calculateStatSummary(statistics.get(i), runMatrix, results);
		}
		
		// for each problem
		for (int i = 0; i < runMatrix.length; i++)
		{			
			// process all statistics for on this problem
			for (int j2 = 0; j2 < statistics.size(); j2++)
			{	
				String problemName = runMatrix[i][0].getProblem().getName();
				String statisticName = statistics.get(j2).getName();
				BoxPlot plot = new BoxPlot();
				plot.setChartLabel(problemName+" and "+statisticName);
				plot.setXAxisLabel("Algorithms");
				plot.setYAxisLabel(statisticName);
				
				// process all algorithms on this problem
				for (int j = 0; j < runMatrix[i].length; j++)
				{
					String algorithmName = runMatrix[i][j].getAlgorithm().getName();
					plot.addBoxAndWhiskerItem(summaries[j2][i][j].getRawResults(), algorithmName, problemName);
				}
				
				// generate the chart (some stupid stats names are rates with a slash)
				statisticName = statisticName.replace('/', '-');
				statisticName = statisticName.replace('\\', '-');
				File chartFile = new File(exp.getExperimentHomeDir() + "/" + problemName+"-"+statisticName+"-chart.png"); 
				try
				{
					ChartUtilities.saveChartAsPNG(chartFile, plot.getChart(), size.width, size.height);
				}
				catch(Exception e)
				{
					throw new RuntimeException("Something bad happened whil preparing chart: "+chartFile.getName(), e);
				}
				System.out.println(">Successfully wrote chart: " + chartFile.getName());
			}
		}
	}
	
	
	public Dimension getChartSize()
	{
		return new Dimension(640, 480);
	}
	
	@Override
	public boolean performAnalysys()
	{
		return true;
	}
	
	@Override
	public void performAnalsys(Experiment experiment)
	{
		super.performAnalsys(experiment);		
		
		ExperimentalRunMatrix matrix = experimentToMatrix(experiment);
		// load results
		RunResult [][][] results = loadRunResults(experiment, matrix);
		// select statistics to report on
		LinkedList<RunProbe> reportStats = getReportingStatistics();	
		// prepare run matrix
		ExperimentalRun [][] runMatrix = runListToRunMatrix(experiment, matrix);
		
		// significance report
		if(useStatisticalSignificanceHack())
		{
			System.out.println(">Generating significance analysis report...");
			generateSignificanceReport(experiment, runMatrix, results, reportStats);
		}
		else
		{
			System.out.println(">Skipping significance analysis...");
		}
		
		// generate charts		
		generateCharts(experiment, runMatrix, results, reportStats);
	}	
	
	
	
	protected String summariseSignificance(RunStatisticSummary [] resultsForMeasure, Experiment exp)
	{
		StatisticalComparisonTest [][] pairwiseResults = pairwiseStatisticalComparisonTests(resultsForMeasure);
		
		// calculate frequencies
		int rejectTrue = 0;
		int rejectFalse = 0;
		for (int i = 0; i < pairwiseResults.length; i++)
		{
			for (int j = i+1; j < pairwiseResults.length; j++)
			{
				if(pairwiseResults[i][j].canRejectNullHypothesis())
				{
					rejectTrue++;
				}
				else
				{
					rejectFalse++;
				}
			}			
		}
		
		StringBuffer b = new StringBuffer();
		
		// check for holistic true outcome
		if(rejectTrue >= rejectFalse)
		{
			b.append("True");
			
			// locate all false
			if(rejectTrue != resultsForMeasure.length)
			{
				for (int i = 0; i < pairwiseResults.length; i++)
				{
					for (int j = i+1; j < pairwiseResults.length; j++)
					{
						if(!pairwiseResults[i][j].canRejectNullHypothesis())
						{
							b.append(", ");
							String r1 = exp.getAlgorithmNameForRunId(resultsForMeasure[i].getExperimentalRunName());
							String r2 = exp.getAlgorithmNameForRunId(resultsForMeasure[j].getExperimentalRunName());
							b.append("False for "+r1+" and "+r2);
						}
					}
				}
			}
		}
		else
		{
			b.append("False");
			
			// locate all true
			if(rejectTrue != resultsForMeasure.length)
			{
				for (int i = 0; i < pairwiseResults.length; i++)
				{
					for (int j = i+1; j < pairwiseResults.length; j++)
					{
						if(pairwiseResults[i][j].canRejectNullHypothesis())
						{
							b.append(", ");
							String r1 = exp.getAlgorithmNameForRunId(resultsForMeasure[i].getExperimentalRunName());
							String r2 = exp.getAlgorithmNameForRunId(resultsForMeasure[j].getExperimentalRunName());
							b.append("True for "+r1+" and "+r2);
						}
					}
				}
			}
		}
		
		return b.toString();
	}
	
	public StatisticalComparisonTest getStatisticalComparisonTestInstance()
	{
		throw new UnsupportedOperationException("getStatisticalComparisonTestInstance() is not supported!");
	}
	
	protected int algorithmStackSize()
	{
		throw new UnsupportedOperationException("algorithmStackSize() is not supported!");
	}
	
	public boolean useStatisticalSignificanceHack()
	{
		return false;
	}
	
	
    public StatisticalComparisonTest [][] pairwiseStatisticalComparisonTests(RunStatisticSummary [] results)
    {
    	StatisticalComparisonTest [][] tests = new StatisticalComparisonTest[results.length][results.length];
    	
    	// across the top
    	for (int x = 0; x < tests.length; x++)
		{
			for (int y = x+1; y < tests[x].length; y++)
			{
				tests[x][y] = getStatisticalComparisonTestInstance();
				try
				{
					tests[x][y].evaluate(results[x], results[y]);
				}
				catch (AnalysisException e)
				{
					throw new RuntimeException("Something unexpected happened calculating pairwise statistics: " + e.getMessage(), e);
				}
			}
		}
    	
    	return tests;
    }
    
	/**
	 * Create a analysis report and output it to csv in the experiment directory 
	 * includes statistical significance hack
	 * 
	 * @param exp
	 * @param runMatrix
	 * @param results
	 * @param statistics
	 */
	public void generateSignificanceReport(
			Experiment exp, 
			ExperimentalRun [][] runMatrix, 
			RunResult [][][] results, 
			LinkedList<RunProbe> statistics)
	{
		int groupSize = algorithmStackSize();
		int numsig = runMatrix[0].length / groupSize;
		
		// pre-calculate all summary statistics
		RunStatisticSummary [][][] summaries = new RunStatisticSummary[statistics.size()][][];
		for (int i = 0; i < summaries.length; i++)
		{
			summaries[i] = calculateStatSummary(statistics.get(i), runMatrix, results);
		}
		
		// CSV result matrix
		NumberFormat f = new DecimalFormat();
		// rows = header + (problems * algorithms) + numsig
		int rows = 1 + (runMatrix.length * runMatrix[0].length) + numsig;
		// cols = p-header + a-header + (stats * 2)
		int cols = (1 + 1 + (statistics.size()*2));
		String [][] csv = new String[rows][cols];
		int col = 0;
		int row = 0;
		
		// add header row
		csv[row][col++] = "Problem";
		csv[row][col++] = "System";
		for (int i = 0; i < statistics.size(); i++)
		{
			// for mean and stdev
			csv[row][col++] = statistics.get(i).getName();
			csv[row][col++] = statistics.get(i).getName();
		}
		row++; // increment row
		
		// for each problem
		boolean perge;
		LinkedList<RunStatisticSummary> [] collection = new LinkedList[statistics.size()];  
		for (int j = 0; j < collection.length; j++)
		{
			collection[j] = new LinkedList<RunStatisticSummary>(); 
		}
		
		for (int i = 0; i < runMatrix.length; i++)
		{			
			perge = true;
			
			// for each algorithm executed on this problem
			for (int j = 0, alg = 0; j < runMatrix[i].length + numsig; j++)
			{				
				col = 0; // reset the columns
				
				if(perge && alg != 0 && (alg%groupSize)==0) 
				{
					csv[row][col++] = "Significant";
					csv[row][col++] = "-";
					
					// calculate significance and purge
					for (int j2 = 0; j2 < statistics.size(); j2++)
					{
						// calculate for this statistic
						String r = summariseSignificance(collection[j2].toArray(new RunStatisticSummary[collection[j2].size()]), exp);	
						csv[row][col++] = r; // significance
						csv[row][col++] = "-"; // skip
						// clear
						collection[j2].clear();
					}
					perge = false;
				}
				else
				{					
					perge = true;
					// add the problem name
					csv[row][col++] = runMatrix[i][alg].getProblem().getName();
					// add the algorithm name
					csv[row][col++] = runMatrix[i][alg].getAlgorithm().getName();
					
					// for each probe of interest collected on this problem-algorithm combination
					for (int j2 = 0; j2 < statistics.size(); j2++)
					{
						// retrieve and format
						csv[row][col++] = f.format(summaries[j2][i][alg].getMean());
						csv[row][col++] = f.format(summaries[j2][i][alg].getStdev());	
						collection[j2].add(summaries[j2][i][alg]);
					}			
					alg++;
				}			
				row++; // increment row	
			}
		}		
		
		System.out.println(">Successfully generated significance report data");

		// output report
		outputCSV("significance-report.csv", exp.getExperimentHomeDir(), csv);
		// output report as latex table
		outputCSVLatexTable("significance-report-latex.txt", exp.getExperimentHomeDir(), csv, numsig);
	}	
	
	
	public void outputCSVLatexTable(String filename, File location, String [][] csv, int numSignificanceLines)
	{
		File outputFile = new File(location + "/" + filename);	
		
		try
		{
			StringBuffer b = new StringBuffer();
			int numLinesPerBlock = (csv.length-1) / numSignificanceLines;
			
			// process all lines
			char [] l = new char[csv[0].length];
			Arrays.fill(l, 'l');
			
			b.append("\\begin{tabular}{"+new String(l)+"}\n");
			for (int i = 0; i < csv.length; i++)
			{
				// header line
				if(i==0)
				{
					b.append("\\hline\n");
					for (int j = 0; j < csv[i].length; j++)
					{
						if(j==0)
						{
							b.append("\\textbf{"+csv[i][j] + "}");
						}
						else if(j==1)
						{
							b.append("\\textbf{"+csv[i][j] + "}");
						}
						else if((j%2)==0)
						{
							b.append("\\multicolumn{2}{c}{\\textbf{"+csv[i][j] + "}}");
							j++; // skip the next one
						}
						else
						{
							// skip							
						}
						
						if(j !=  csv[i].length-1)
						{
							b.append(" & ");
						}
					}
					b.append("\\\\\n\\hline\n");
					// now output a mean standard deviation thing
					for (int j = 0; j < csv[i].length; j++)
					{
						if(j==0)
						{
							b.append("\\emph{EP}");	
						}
						else if(j==1)
						{
							b.append("\\emph{CSA}");	
						}
						else if((j%2)==0)
						{
							// mean
							b.append("$\\bar{x}$");
						}
						else
						{
							// stdev
							b.append("$\\sigma$");
						}
						if(j !=  csv[i].length-1)
						{
							b.append(" & ");
						}
					}					
					b.append("\\\\\n\\hline");
				}
				// significance line
				else if((i % numLinesPerBlock)==0)
				{
					for (int j = 0; j < csv[i].length; j++)
					{
						if(j==0)
						{
							b.append("\\multicolumn{2}{l}{\\emph{Significant}}");					
						}
						else if(j==1)
						{
							// skip and do not add a thingo (&)
							continue;
						}
						else if((j%2)==0)
						{
							String s = csv[i][j];
							int index = s.indexOf(',');
							if(index != -1)
							{
								s = s.replace('_', '-');
								s = s.substring(0, index)+"\\footnote{"+s.substring(index+2)+"}";								
							}
							
							b.append(s);
						}
						
						if(j != csv[i].length-1)
						{
							b.append(" & ");
						}
					}
					b.append("\\\\\n\\hline");
				}
				// normal line
				else
				{
					for (int j = 0; j < csv[i].length; j++)
					{
						if(j==0)
						{
							b.append(csv[i][j]); // problem name
						}
						else if(j==1)
						{
							String s = csv[i][j];
							s = s.replace('_', '-');
							b.append(s);
						}
						else
						{
							b.append(csv[i][j]);
						}
						
						if(j != csv[i].length-1)
						{
							b.append(" & ");
						}
						else
						{
							b.append(" \\\\");
						}
					}
				}
				
				b.append("\n");
			}
			b.append("\\end{tabular}");
			
			FileUtils.writeToFile(b.toString(), outputFile);
		}
		catch (Exception e1)
		{
			throw new RuntimeException("Something unexpected happened while outputting significance report latex file.", e1);
		}
		System.out.println(">Successfully saved significance report data to file: " + outputFile.getName());
	}
	
	
	public static void outputCSV(String filename, File location, String [][] csv)
	{
		File outputFile = new File(location + "/" + filename);	
		try
		{				
			FileUtils.writeCSV(csv, outputFile);
		}
		catch (Exception e1)
		{
			throw new RuntimeException("Something unexpected happened while outputting significance report.", e1);
		}
		System.out.println(">Successfully saved significance report data to file: " + outputFile.getName());
	}
}
