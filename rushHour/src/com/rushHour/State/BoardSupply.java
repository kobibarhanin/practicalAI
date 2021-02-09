package com.rushHour.State;

import java.util.ArrayList;

public class BoardSupply {

    public String inputBoards = "AA...OP..Q.OPXXQ.OP..Q..B...CCB.RRR.;" +
            "A..OOOA..B.PXX.BCPQQQ.CP..D.EEFFDGG.;" +
            ".............XXO...AAO.P.B.O.P.BCC.P;" +
            "O..P..O..P..OXXP....AQQQ..A..B..RRRB;" +
            "AA.O.BP..OQBPXXOQGPRRRQGD...EED...FF;" +
            "AA.B..CC.BOP.XXQOPDDEQOPF.EQ..F..RRR;" +
            ".ABBCD.A.ECD.XXE.F..II.F...H.....H..;" +
            "...AAO..BBCOXXDECOFFDEGGHHIPPPKKIQQQ;" +
            ".ABBCC.A.DEEXX.DOFPQQQOFP.G.OHP.G..H;" +
            "AAB.CCDDB..OPXX..OPQQQ.OP..EFFGG.EHH;" +
            "OAAP..O..P..OXXP....BQQQ..B..E..RRRE;" +
            "ABB..OA.P..OXXP..O..PQQQ....C.RRR.C.;" +
            "AABBC...D.CO.EDXXOPE.FFOP..GHHPIIGKK;" +
            "AAB.....B.CCDEXXFGDEHHFG..I.JJKKI...;" +
            ".AABB.CCDDOPQRXXOPQREFOPQREFGG.HHII.;" +
            "AABBCOD.EECODFPXXO.FPQQQ..P...GG....;" +
            "AOOO..A.BBCCXXD...EEDP..QQQPFGRRRPFG;" +
            "AABO..CCBO..PXXO..PQQQ..PDD...RRR...;" +
            "..ABB...A.J..DXXJ..DEEF..OOOF.......;" +
            "A..OOOABBC..XXDC.P..D..P..EFFP..EQQQ;" +
            "AABO..P.BO..PXXO..PQQQ...........RRR;" +
            "..AOOOB.APCCBXXP...D.PEEFDGG.HFQQQ.H;" +
            "..OOOP..ABBP..AXXP..CDEE..CDFF..QQQ.;" +
            "..ABB..CA...DCXXE.DFF.E.OOO.G.HH..G.;" +
            "AAB.CCDDB..OPXX.EOPQQQEOPF.GHH.F.GII;" +
            ".A.OOOBA.CP.BXXCPDERRRPDE.F..G..FHHG;" +
            "ABBO..ACCO..XXDO.P..DEEP..F..P..FRRR;" +
            "OOOA....PABBXXP...CDPEEQCDRRRQFFGG.Q;" +
            "OOO.P...A.P.XXA.PBCDDEEBCFFG.HRRRG.H;" +
            "O.APPPO.AB..OXXB..CCDD.Q.....QEEFF.Q;" +
            "AA.OOO...BCCDXXB.PD.QEEPFFQ..P..QRRR;" +
            "AAOBCC..OB..XXO...DEEFFPD..K.PHH.K.P;" +
            ".AR.BB.AR...XXR...IDDEEPIFFGHPQQQGHP;" +
            "A..RRRA..B.PXX.BCPQQQDCP..EDFFIIEHH.;" +
            "..OAAP..OB.PXXOB.PKQQQ..KDDEF.GG.EF.;" +
            "OPPPAAOBCC.QOBXX.QRRRD.Q..EDFFGGE...;" +
            "AAB.CCDDB.OPQXX.OPQRRROPQ..EFFGG.EHH;" +
            "A..OOOABBC..XXDC.R..DEER..FGGR..FQQQ;" +
            "..AOOO..AB..XXCB.RDDCEERFGHH.RFGII..;" +
            "OAA.B.OCD.BPOCDXXPQQQE.P..FEGGHHFII.";

    public ArrayList<State> states = new ArrayList<State>();
    public String[] boardsStrings;

    public BoardSupply() {
        boardsStrings = inputBoards.split(";");
        for (int i=0; i<boardsStrings.length; i++)
            states.add(new State(6,boardsStrings[i]));
    }
}
