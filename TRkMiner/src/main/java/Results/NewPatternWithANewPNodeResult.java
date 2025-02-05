package Results;

import Base.PNode;
import Base.Pattern;

/**
 * @author HappyCower
 * @creed: class 封装复制一个新模式同时返回对应的推展的点的新地址的结果
 * @date 2022/3/19 20:27
 */
public class NewPatternWithANewPNodeResult {
    Pattern newPattern;
    PNode pNode;

    public NewPatternWithANewPNodeResult(Pattern newPattern, PNode pNode) {
        this.newPattern = newPattern;
        this.pNode = pNode;
    }

    public Pattern getNewPattern() {
        return newPattern;
    }

    public void setNewPattern(Pattern newPattern) {
        this.newPattern = newPattern;
    }

    public PNode getpNode() {
        return pNode;
    }

    public void setpNode(PNode pNode) {
        this.pNode = pNode;
    }
}
