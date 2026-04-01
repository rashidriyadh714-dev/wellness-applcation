package wellness.ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import wellness.model.AbstractRecord;

public class RecordTableModel extends AbstractTableModel {
    private final String[] columns = {"Record ID", "Date", "Type", "Impact Score", "Notes"};
    private List<AbstractRecord> records = new ArrayList<AbstractRecord>();

    public void setRecords(List<AbstractRecord> records) {
        this.records = new ArrayList<AbstractRecord>(records);
        fireTableDataChanged();
    }

    public AbstractRecord getRecordAt(int rowIndex) {
        return records.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AbstractRecord record = records.get(rowIndex);
        switch (columnIndex) {
            case 0: return record.getRecordId();
            case 1: return record.getDate();
            case 2: return record.getRecordType();
            case 3: return String.format("%.1f", record.calculateImpactScore());
            case 4: return record.getNotes();
            default: return "";
        }
    }
}
