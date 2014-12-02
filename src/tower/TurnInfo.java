package tower;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.TreeMap;

public class TurnInfo {
	public Integer turn;
	public String unitName;
	public Integer numberOfUnits;

	public static Map<Integer, TurnInfo> turns = new TreeMap<>();

	static {

		try {
			// 0=길, 1=타워건설가능, 2=시작지점, 3=종료지점.
			FileReader fr = new FileReader("turn-info");
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				TurnInfo t = new TurnInfo();
				// 줄단위로 돌면서
				String[] info = line.split("\t");
				// 1은 턴.
				t.turn = Integer.parseInt(info[0]);
				// 2는 유닛이름.
				t.unitName = info[1];
				// 3은 속도
				t.numberOfUnits = Integer.parseInt(info[2]);

				// 저장.
				turns.put(t.turn, t);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "TurnInfo [turn=" + turn + ", unitName=" + unitName
				+ ", numberOfUnits=" + numberOfUnits + "]";
	}

}
