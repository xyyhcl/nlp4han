package com.lc.nlp4han.constituent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import com.lc.nlp4han.ml.util.FileInputStreamFactory;

/**
 * CTB树库清理程序
 * 
 * 可以去掉空节点、去掉功能标记、删除单元规则结构
 * 
 * @author 刘小峰
 *
 */
public class CTBClearerTool
{
	// 去掉空节点
	private static void removeNullNode(TreeNode node)
	{
		String nodeName = node.getNodeName();
		if (nodeName.equals("-NONE-"))
		{
			node.setFlag(false);
			node.getFirstChild().setFlag(false);
			node.getParent().setFlag(false);
		}

		List<? extends TreeNode> children = node.getChildren();
		for (TreeNode child : children)
		{
			removeNullNode(child);
		}
	}

	// 去掉句法功能标记
	private static void removeFuncTag(TreeNode node)
	{
		if (node.getChildrenNum() == 0)
			return;

		String nodeName = node.getNodeName();
		int index = nodeName.indexOf('-');
		if (index > 0 && index < nodeName.length() - 1 && Character.isLetter(nodeName.charAt(index + 1)))
		{
			String n = nodeName.substring(0, index);
			node.setNewName(n);
		}

		List<? extends TreeNode> children = node.getChildren();
		for (TreeNode child : children)
		{
			removeFuncTag(child);
		}
	}

	private static int indexOf(TreeNode parent, TreeNode child)
	{
		int index = -1;
		List<? extends TreeNode> children = parent.getChildren();
		for (int i = 0; i < children.size(); i++)
		{
			if (child == children.get(i))
			{
				index = i;

				return index;
			}
		}

		return index;
	}

	// 去掉单元规则结构
	private static void removeUnit(TreeNode node)
	{
		TreeNode parent = node.getParent();

		if (node.getChildrenNum() == 1)
		{
			TreeNode child = node.getFirstChild();
			if (node.getNodeName().equals(child.getNodeName()) && parent!=null)
			{
				int index = indexOf(parent, node);
				
				parent.setChild(index, child);
				child.setParent(parent);
			}
		}
		else if (node.getChildrenNum() > 1)
		{
			List<? extends TreeNode> children = node.getChildren();
			for (TreeNode child : children)
			{
				removeUnit(child);
			}
		}
	}
	
	// 在最外层添加ROOT
	private static String addRoot(String str)
	{
		String res = "(ROOT " + str + ")";
		
		return res;
	}
	
	private static void usage()
	{
		System.out.println(CTBClearerTool.class.getName()
				+ " <inBracketFile> <outBracketFile> <encoding>");
	}

	public static void main(String[] args) throws IOException
	{
		if(args.length <1)
		{
			usage();
			
			return;
		}
		
		String encoding = args[2];
		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(
				new FileInputStreamFactory(new File(args[0])), encoding);
		String bracketStr = "";
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), encoding));
		int n = 1;
		while ((bracketStr = lineStream.read()) != "")
		{
			System.out.println(n);
			
			TreeNode tree = BracketExpUtil.generateTreeNotDeleteBracket(bracketStr);

			removeFuncTag(tree);

			removeNullNode(tree);
			
			String str = tree.toStringNoNone();
			tree = BracketExpUtil.generateTreeNotDeleteBracket(str);
			removeUnit(tree);

			str = tree.toStringNoNone();			
			str = addRoot(str);
			
			tree = BracketExpUtil.generateTreeNotDeleteBracket(str);
			
			out.println(TreeNode.printTree(tree, 1));
			
			n++;
		}

		lineStream.close();
		out.close();

	}

}
