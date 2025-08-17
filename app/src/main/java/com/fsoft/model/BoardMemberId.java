package com.fsoft.model;

import java.io.Serializable;
import java.util.UUID;

// You'll also need this class for the composite key
public class BoardMemberId implements Serializable {
    private UUID boardId;
    private UUID userId;

}
