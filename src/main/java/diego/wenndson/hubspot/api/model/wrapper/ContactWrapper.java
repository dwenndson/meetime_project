package diego.wenndson.hubspot.api.model.wrapper;

import diego.wenndson.hubspot.api.model.Contact;

public  class ContactWrapper {
    private ContactProperties properties;

    public ContactWrapper(Contact contact) {
        this.properties = new ContactProperties();
        this.properties.email = contact.getEmail();
        this.properties.firstname = contact.getFirstName();
        this.properties.lastname = contact.getLastName();
    }

    public ContactProperties getProperties() {
        return properties;
    }

    public void setProperties(ContactProperties properties) {
        this.properties = properties;
    }

    static class ContactProperties {
        public String email;
        public String firstname;
        public String lastname;
    }
}