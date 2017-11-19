package higa.sharedcalendars.model;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

@Table
public class Memo {

    @PrimaryKey(autoincrement = true)
    public int id;

    @Column(indexed = true)
    public String memoTitle;
    
    @Column (indexed = true)
    public String memoContent;

    @Override
    public String toString() {
        return "Memo{" +
                "id=" + id +
                ", memoTitle='" + memoTitle + '\'' +
                ", memoContent=" + memoContent +
                '}';
    }
}
