package kkr.ktm.domains.common.components.parametersformater.template;

import java.util.ArrayList;
import java.util.List;

public class Position {
	private List<String> contentLines = new ArrayList<String>();
	private int iLine = 0;
	private int iPosition = 0;
	private int iPositionTotal = 0;

	public Position(String content) {
		setContent(content);
		this.iPosition = 0;
	}

	private Position(List<String> contentLines, int positionTotal) {
		this.contentLines = contentLines;
		this.iPositionTotal = positionTotal;

		if (positionTotal == 200) {
			int k = 0;
		}

		int positionTotalCurrent = 0;
		for (iLine = 0; iLine < contentLines.size() && positionTotalCurrent <= positionTotal; iLine++) {
			int lineLength = contentLines.get(iLine).length();
			if (positionTotalCurrent + lineLength < positionTotal) {
				positionTotalCurrent += lineLength + 1;
			} else {
				this.iPosition = positionTotal - positionTotalCurrent;
				return;
			}
		}
		throw new IllegalArgumentException(
				"Total position is out of bound: " + positionTotal + " of " + positionTotalCurrent);
	}

	private void setContent(String content) {
		int iPos = 0;
		for (int iPosNl; (iPosNl = content.indexOf('\n', iPos)) != -1; iPos = iPosNl + 1) {
			String line = content.substring(iPos, iPosNl);
			contentLines.add(line);
		}
		if (iPos < content.length()) {
			String line = content.substring(iPos);
			contentLines.add(line);
		}
	}

	public int getPosition() {
		return iPosition;
	}

	public Position movePosition(int move) {
		if (move != 0) {
			return new Position(contentLines, iPositionTotal + move);
		} else {
			return this;
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("[" + String.valueOf(iLine) + "," + String.valueOf(iPosition) + "]");
		if (contentLines != null && iLine < contentLines.size()) {
			String contentLine = contentLines.get(iLine);
			buffer.append("\n" + contentLine + "\n");
			for (int i = 0; i < contentLine.length(); i++) {
				if (i + 1 != contentLine.length() || contentLine.charAt(i) != '\r') {
					if (i != iPosition) {
						buffer.append('_');
					} else {
						buffer.append('^');
					}
				}
			}
			buffer.append('\n');
		}
		return buffer.toString();
	}
}
