package comp3111.covid;

public class DataPicker<T> {
    T data[];

    public DataPicker(T dataList[]) {
        this.data = dataList;
    }

    public T[] possibleOptions() {
        return data;
    }

    public T pick(int index) {
        return data[index];
    }
}
