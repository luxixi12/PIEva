package Results;

import Base.PNode;
import Base.Pattern;

/**
 *
 * @author HappyCower
 * @creed: class 封装一个返回一个新模式和模式中的两个新点的结果
 * @date 2022/3/24 11:38
 */

public class NewPatternWithTwoNewPNodeResult {
        Pattern newPattern;
        PNode inNode;
        PNode outNode;

        public NewPatternWithTwoNewPNodeResult(Pattern newPattern, PNode inNode,PNode outNode) {
            this.newPattern = newPattern;
            this.inNode = inNode;
            this.outNode = outNode;
        }

    public Pattern getNewPattern() {
        return newPattern;
    }

    public void setNewPattern(Pattern newPattern) {
        this.newPattern = newPattern;
    }

    public PNode getInNode() {
        return inNode;
    }

    public void setInNode(PNode inNode) {
        this.inNode = inNode;
    }

    public PNode getOutNode() {
        return outNode;
    }

    public void setOutNode(PNode outNode) {
        this.outNode = outNode;
    }
}

