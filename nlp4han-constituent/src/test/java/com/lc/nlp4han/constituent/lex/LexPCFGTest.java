package com.lc.nlp4han.constituent.lex;


import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class LexPCFGTest
{
	private LexPCFG lexpcfg;
	private String word;
	private RuleHeadChildGenerate rhcg0;
	private RuleHeadChildGenerate rhcg1;
	private RuleStopGenerate rsg;
	private RuleSidesGenerate sidesRule;
	private RuleSidesGenerate npbRule;
	private RuleSpecialCase specialRule;
	private int f,u;
	@Before
	public void beforeTest() {
		lexpcfg = new LexPCFG();
	}
	@Test
	public void testLexPCFG()
	{
		//测试得到起始符
		String startSymbol=lexpcfg.getStartSymbol();
		
		//测试得到词性标注集合
	    HashSet<String> set=lexpcfg.getPosSet();

		//测试得到某个词词在训练数据集中出现的pos集合
		 HashSet<String> posSetOfword=lexpcfg.getposSetByword(word);

		//测试由headChild得到可行的Parent/或者向上延伸的单元规则
		HashSet<String> parentSet=lexpcfg.getParentSet(rhcg0);

		//测试得到P（H|P,(pos,word)）的概率/可以用于计算单元规则的概率
		double pro1=lexpcfg.getProForGenerateHead(rhcg1);

		 //测试得到生成Pl/Pr(Stop|P,H,(t,w),distance)的概率
		double pro2=lexpcfg.getProForGenerateStop(rsg);

		 //测试得到生成两侧即Pl/Pr(L(lpos,lword)c,p|P,H,(hpos,hword),distance)的概率c和p分别指CC、标点符号（此处专指顿号）
		double pro3=lexpcfg.getProForGenerateSides(sidesRule);

		//得到生成NPB(基本名词短语)两侧的概率 即Pl/Pr(L(lpos,lword)|P,preModifer,preM(pos,word))
		double pro4=lexpcfg.getProForGenerateNPBSides(npbRule);

		/**
		 * 得到并列结构（CC）或者含有顿号结构的概率
		 * 即P(CC,word|P,leftLabel,rightLabel,leftWord,righrWord)的概率
		 */
		double pro5=lexpcfg.getProForSpecialCase(specialRule);

		//得到用于平滑运算的λ值
		double pro6=lexpcfg.getProByPOS(f,u);
	}
}
