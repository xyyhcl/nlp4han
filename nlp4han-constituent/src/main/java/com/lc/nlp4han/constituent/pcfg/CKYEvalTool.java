package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.IOException;
import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

public class CKYEvalTool
{
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		if (args.length < 1)
		{
			return;
		}
		
		String trainFile = null;
		String goldFile = null;
		String encoding = null;
		double pruneThreshold=0.0001;
		boolean secondPrune=false;
		boolean prior=false;
		boolean segmentPrune=false;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-model"))
			{
				trainFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-gold"))
			{
				goldFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
			else if (args[i].equals("-pruneThreshold"))
			{
				pruneThreshold =Double.parseDouble(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-secondPrune"))
			{
				secondPrune = Boolean.parseBoolean(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-prior"))
			{
				prior = Boolean.parseBoolean(args[i + 1]);
				i++;
			}
			else if (args[i].equals("-segmentPrune"))
			{
				segmentPrune = Boolean.parseBoolean(args[i + 1]);
				i++;
			}
		}
		eval(trainFile, goldFile, encoding,pruneThreshold,secondPrune,prior,segmentPrune);
	}

	public static void eval(String trainFile, String goldFile, String encoding,double pruneThreshold,boolean secondPrune,boolean prior,boolean segmentPrune) throws IOException, ClassNotFoundException
	{
		PCFG pcnf=CFGModelIOUtil.loadPCFGModel(trainFile); 
        
		CKYParserEvaluator evaluator = new CKYParserEvaluator(pcnf,"pcnf",pruneThreshold,secondPrune,prior,segmentPrune);
		
		ConstituentMeasure measure = new ConstituentMeasure();
		evaluator.setMeasure(measure);
		
		ObjectStream<String> treeStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(goldFile)),
				encoding);
		ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(treeStream);
		evaluator.evaluate(sampleStream);
		
		ConstituentMeasure measureRes = evaluator.getMeasure();
		System.out.println(measureRes);
	}

}
