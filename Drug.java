import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class Drug {
    private String Name;
    private int qty;
    private double num_boxes; // qty / 80
    private String description;
    private String expirationDate;
    private int category;
    private String categoryLabel;
    private double price;
    private String notes; // for low qty or expo coming up or physical error
    private String location;

    public Drug() {
        this.qty = 0;
        this.num_boxes = 0;
        this.price = 0;
    }

    public Drug(String name, int qty, double num_boxes, String description, String expirationDate, int category, String catLab, double price, String notes, String location) {
        this.Name = name;
        this.qty = qty;
        this.num_boxes = num_boxes;
        this.description = description;
        this.expirationDate = expirationDate;
        this.category = category;
        this.categoryLabel = catLab;
        this.price = price;
        this.notes = notes;
        this.location = location;

        this.setCategoryLabel();
        this.setNum_boxes();
        this.lowStockReminder();
    }

    public String getName() { return Name; }
    public int getQty() { return qty; }
    public double getNum_boxes() { return num_boxes; }
    public String getDescription() { return description; }
    public String getExpirationDate() { return expirationDate; }
    public int getCategory() { return category; }
    public String getCategoryLabel() { return categoryLabel; }
    public double getPrice() { return price; }
    public String getNotes() { return notes; }
    public String getLocation() { return location; }

    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public void addQty(int qty) {
        this.qty += qty;
        this.setNum_boxes();
        this.lowStockReminder();
    }

    public void setNum_boxes() {
        this.num_boxes = this.qty / 80.0;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void lowStockReminder() {
        // Ensure "LOW STOCK" is always displayed if quantity <= 120
        if (qty <= 120) {
            if (!notes.contains("LOW STOCK")) {
                notes = "LOW STOCK"; // Add "LOW STOCK"
            }
        } else {
            notes = notes.replace("LOW STOCK", "").trim(); // Remove "LOW STOCK"
        }
    }

    public void updateQuantity(int quantityToAdd, String reason) {
        this.qty += quantityToAdd;
        this.setNum_boxes(); // Recalculate the number of boxes
        this.lowStockReminder(); // Update "LOW STOCK" status
        // Add the most recent reason
        if (qty <= 120) {
            notes = "LOW STOCK | Reason for change: " + reason;
        } else {
            notes = "Reason for change: " + reason;
        }
    }

    public void reduceQuantity(int quantityToRemove, String reason) {
        if (quantityToRemove > this.qty) {
            System.out.println("Error: Cannot remove more than the available quantity!");
        } else {
            this.qty -= quantityToRemove;
            this.setNum_boxes(); // Recalculate the number of boxes
            this.lowStockReminder(); // Update "LOW STOCK" status
            // Add the most recent reason
            if (qty <= 120) {
                notes = "LOW STOCK | Reason for change: " + reason;
            } else {
                notes = "Reason for change: " + reason;
            }
        }
    }

    public LocalDate getExpirationDateAsLocalDate() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            return LocalDate.parse(this.expirationDate, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid expiration date format for: " + this.Name);
            return LocalDate.MAX; // Use a very far date if parsing fails
        }
    }

    public void setCategoryLabel() {

        switch(this.category) {
            case 1:
                this.categoryLabel = "Prescription Drug";
                break;
            case 2:
                this.categoryLabel = "Non-Prescription Drug";
                break;
            case 3:
                this.categoryLabel = "Non-Drug Item";
                break;
            default:
                this.categoryLabel = "";
        }
    }

    public String toString() {
        return "Name: " + this.Name + "\nQty: " + this.qty + "\nDescription: " + this.description + "\nExpiration Date: " + this.expirationDate + "\nItem Type: " + this.categoryLabel + "\nPrice: $" + this.price + "\nNotes: " + this.notes;
    }

    public String toCsvString() {
        return this.Name.replace(",", ";") + "," +
                this.qty + "," + this.num_boxes + "," +
                this.description.replace(",", ";") + "," +
                this.expirationDate.replace(",", ";") + "," +
                this.category + "," +
                this.categoryLabel.replace(",", ";") + "," +
                this.price + "," +
                this.notes.replace(",", ";") + "," +
                this.location.replace(",", ";");
    }
}
