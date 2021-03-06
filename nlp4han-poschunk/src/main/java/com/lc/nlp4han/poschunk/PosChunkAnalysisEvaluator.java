package com.lc.nlp4han.poschunk;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisMeasure;
import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.ChunkAnalysisEvaluateMonitor;
import com.lc.nlp4han.ml.util.Evaluator;

/**
 * 基于词的词性标注和组块分析评价器
 */
public class PosChunkAnalysisEvaluator extends Evaluator<AbstractChunkAnalysisSample>
{

	/**
	 * 组块分析模型
	 */
	private PosChunkAnalysisWordME chunkTagger;

	/**
	 * 组块分析评估
	 */
	private AbstractChunkAnalysisMeasure measure;

	/**
	 * 词性标注评估
	 */
	private POSBasedWordMeasure posMeasure;

	/**
	 * 构造方法
	 * 
	 * @param tagger
	 *            训练得到的模型
	 */
	public PosChunkAnalysisEvaluator(PosChunkAnalysisWordME chunkTagger)
	{
		this.chunkTagger = chunkTagger;
	}

	/**
	 * 构造方法
	 * 
	 * @param tagger
	 *            训练得到的模型
	 * @param evaluateMonitors
	 *            评估的监控管理器
	 */
	public PosChunkAnalysisEvaluator(PosChunkAnalysisWordME chunkTagger, AbstractChunkAnalysisMeasure measure,
			ChunkAnalysisEvaluateMonitor... evaluateMonitors)
	{
		super(evaluateMonitors);
		this.chunkTagger = chunkTagger;
		this.measure = measure;
	}

	/**
	 * 设置评估指标的对象
	 * 
	 * @param measure
	 *            评估指标计算的对象
	 */
	public void setMeasure(AbstractChunkAnalysisMeasure measure)
	{
		this.measure = measure;
	}

	public void setMeasure(POSBasedWordMeasure posMeasure)
	{
		this.posMeasure = posMeasure;
	}

	/**
	 * 得到评估的指标
	 * 
	 * @return
	 */
	public AbstractChunkAnalysisMeasure getMeasure()
	{
		return measure;
	}

	@Override
	protected AbstractChunkAnalysisSample processSample(AbstractChunkAnalysisSample sample)
	{
		String[] wordsRef = sample.getTokens();
		String[] posChunksRef = sample.getTags();

		String[] posChunksPre = chunkTagger.tag(wordsRef);

		// 将结果进行解析，用于评估
		PosChunkAnalysisSample prediction = new PosChunkAnalysisSample(wordsRef, posChunksPre);
		prediction.setTagScheme(sample.getTagScheme());
		String[] chunksPre = new String[posChunksPre.length];
		String[] chunksRef = new String[posChunksPre.length];
		String[] posRef = new String[posChunksRef.length];
		String[] posPre = new String[posChunksPre.length];
		for (int i = 0; i < chunksPre.length; i++)
		{
			posRef[i] = posChunksRef[i].split("-")[0];
			posPre[i] = posChunksPre[i].split("-")[0];
			chunksPre[i] = posChunksPre[i].split("-")[1];
			chunksRef[i] = posChunksRef[i].split("-")[1];
		}

		measure.update(wordsRef, chunksRef, chunksPre);
		posMeasure.updateScores(wordsRef, posRef, posPre);
		// measure.add(sample, prediction);
		return prediction;
	}
}
