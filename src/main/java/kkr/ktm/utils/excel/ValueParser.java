package kkr.ktm.utils.excel;

import java.util.ArrayList;
import java.util.List;

public class ValueParser {

	static interface Part {

	}

	static class Open implements Part {
		public String toString() {
			return "[Open]";
		}
	}

	static class Close implements Part {
		public String toString() {
			return "[Close]";
		}
	}

	static class Separ implements Part {
		public String toString() {
			return "[Separ]";
		}
	}

	static class Text implements Part {
		private String text;

		Text(String pText) {
			if (pText == null) {
				this.text = null;
			} else {
				this.text = pText.replace('\n', ' ').replace("\r", "").trim();
			}
		}

		public String getText() {
			return text;
		}

		public String toString() {
			return "[Text:" + text + "]";
		}
	}

	public static Value parseValue(String text) throws Exception {
		int phase = 0;
		int iPos = 0;
		int beg = 0;

		List<String> flags = new ArrayList<String>();

		go_for: for (iPos = 0; iPos < text.length(); iPos++) {
			char c = text.charAt(iPos);
			switch (phase) {
			case 0:
				if (c != '<') {
					break go_for;
				}
				phase = 1;
				beg = iPos + 1;
				break;
			case 1:
				if (!Character.isLetter(c) && c != '_') {
					throw new Exception("Syntax flags brackets <>");
				}
				phase = 2;
				break;
			case 2:
				if (c == '>') {
					flags.add(text.substring(beg, iPos));
					phase = 0;
				} else if (!Character.isLetterOrDigit(c) && c != '_') {
					throw new Exception("Syntax flags brackets <>");
				}
				break;
			}
		}

		String value = text.substring(iPos);
		final List<Part> parts = createParts(value);
		checkParts(parts);
		Object tree = createTree(parts);
		adaptTree(tree, 0);
		
		Value retval = new Value();
		retval.getFlags().addAll(flags);
		retval.setValue(tree);
		return retval;
	}

	private static List<Part> createParts(String pValue) {
		final List<Part> parts = new ArrayList<Part>();
		boolean mask = false;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < pValue.length(); i++) {
			char c = pValue.charAt(i);
			if (mask) {
				buffer.append(c);
				mask = false;
				continue;
			}
			switch (c) {
			case '[':
				if (buffer.length() != 0) {
					if (!buffer.toString().trim().isEmpty()) {
						parts.add(new Text(buffer.toString()));
					}
					buffer = new StringBuffer();
				}
				parts.add(new Open());
				break;
			case ']':
				if (buffer.length() != 0) {
					if (!buffer.toString().trim().isEmpty()) {
						parts.add(new Text(buffer.toString()));
					}
					buffer = new StringBuffer();
				}
				parts.add(new Close());
				break;
			case '|':
				if (buffer.length() != 0) {
					if (!buffer.toString().trim().isEmpty()) {
						parts.add(new Text(buffer.toString()));
					}
					buffer = new StringBuffer();
				}
				parts.add(new Separ());
				break;
			case '\\':
				mask = true;
				break;
			default:
				buffer.append(c);
			}
		}
		if (buffer.length() != 0) {
			parts.add(new Text(buffer.toString().trim()));
		}
		return parts;
	}

	private static void checkParts(List<Part> pParts) throws Exception {
		final List<Part> parts = new ArrayList<Part>();
		Part last = null;
		int level = 0;
		for (Part part : pParts) {
			if (last != null) {
				if (last instanceof Open) {
					if (part instanceof Close || part instanceof Separ) {
						parts.add(new Text(""));
					}
				} else if (last instanceof Separ) {
					if (part instanceof Separ || part instanceof Close) {
						parts.add(new Text(""));
					}
				} else if (last instanceof Text) {
					if (part instanceof Open) {
						throw new Exception("Syntax using brackets");
					}
				} else if (last instanceof Close) {
					if (part instanceof Text || part instanceof Open) {
						throw new Exception("Syntax using brackets");
					}
				}
			}
			parts.add(part);

			if (part instanceof Open) {
				level++;
			} else if (part instanceof Close) {
				level--;
				if (level < 0) {
					throw new Exception("Close bracket without Open bracket");
				}
			}

			last = part;
		}

		if (level != 0) {
			throw new Exception("Open bracket without Close bracket");
		}

		if (pParts.size() == 0) {
			pParts.add(new Text(""));
		} else if (pParts.size() == 1) {
			if (!(pParts.get(0) instanceof Text)) {
				throw new Exception("Syntaxe problem");
			}
		} else if (!(pParts.get(0) instanceof Open)
				|| !(pParts.get(pParts.size() - 1) instanceof Close)) {
			throw new Exception("Syntaxe problem");
		}

		pParts.clear();
		pParts.addAll(parts);
	}

	private static Object createTree(List<Part> pParts) throws Exception {
		final List<Object> tree = new ArrayList<Object>();

		if (pParts.size() == 1) {
			return new String[] { ((Text) pParts.get(0)).getText() };
		}

		for (int i = 1; i < pParts.size() - 1; i++) {
			if (pParts.get(i) instanceof Open) {
				final List<Part> localParts = new ArrayList<Part>();
				localParts.add(pParts.get(i));
				int level = 1;
				for (i++; level != 0 && i < pParts.size(); i++) {
					localParts.add(pParts.get(i));
					if (pParts.get(i) instanceof Open) {
						level++;
					} else if (pParts.get(i) instanceof Close) {
						level--;
					}
				}
				final Object object = createTree(localParts);
				tree.add(object);
			} else if (pParts.get(i) instanceof Text) {
				tree.add(((Text) pParts.get(i)).getText());
			}
		}

		return tree.toArray(new Object[tree.size()]);
	}

	private static void adaptTree(Object pObject, int pLevel) throws Exception {
		if (pObject != null && pObject.getClass().isArray()) {
			final Object[] array = (Object[]) pObject;
			int countArrays = 0;
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null && array[i].getClass().isArray()) {
					countArrays++;
				}
			}
			if (countArrays != 0) {
				if (countArrays != array.length) {
					throw new Exception("All elements on the level " + pLevel
							+ " must be arrays");
				}
				for (int i = 0; i < array.length; i++) {
					final Object[] localArray = (Object[]) array[i];
					adaptTree(localArray, pLevel + 1);
				}
			}
		}
	}
}
