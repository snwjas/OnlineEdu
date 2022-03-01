package xyz.refrain.onlineedu.model.params;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 排序参数
 *
 * @author Myles Yang
 */
@SuppressWarnings("unchecked")
public class OrderBy<R> {

	private final List<R> asc;

	private final List<R> desc;

	public OrderBy() {
		this.asc = new ArrayList<>();
		this.desc = new ArrayList<>();
	}

	public OrderBy<R> asc(R... col) {
		asc.addAll(Arrays.asList(col));
		return this;
	}

	public OrderBy<R> desc(R... col) {
		desc.addAll(Arrays.asList(col));
		return this;
	}

	public R[] getAsc() {
		return (R[]) asc.toArray();
	}

	public R[] getDesc() {
		return (R[]) desc.toArray();
	}

}
