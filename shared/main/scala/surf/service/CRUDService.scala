// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Provides a base class for CRUD data services
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.service

import surf.Service

/**
 * Base class for services providing CRUD operations on data entities.
 *
 * @tparam IdType type of entity IDs
 * @tparam EntityType type of entities handled by this service
 */
abstract class CRUDService[IdType,EntityType] extends Service {
  import CRUDService._

  /**
   * Checks and casts the specified entity ID to the defined IdType
   *
   * @param id ID to be checked
   *
   * @throws InvalidId if the id is invalid
   */
  def checkId(id: Any) : IdType

  /**
   * Checks and casts the specified entity to the defined EntityType.
   *
   * @param entity Entity to be checked
   *
   * @throws InvalidEntity if the entity is invalid
   */
  def checkEntity(entity: Any) : EntityType

  /**
   * Called whenever the result of an operation is a collection of entities.
   * The return value of this method is used as the response to the request.
   * Wrapping a generic collection is often useful, since the type parameter is erased at runtime.
   *
   * @param response The collection to be wrapped
   */
  def wrapListResponse(response: Iterable[EntityType]) : Any

  /**
   * Returns an Iterable with all defined entities.
   *
   * @throws CRUDServiceError if the request could not be processed
   * @throws OperationNotSupported if this operation is not supported by the service implementation
   */
  def listEntities() : Iterable[EntityType] = throw new OperationNotSupported("listEntities")

  /**
   * Returns the entity with the specified ID, or None if the entity does not exist.
   *
   * @param id The ID of the requested entity
   *
   * @throws CRUDServiceError if the request could not be processed
   * @throws OperationNotSupported if this operation is not supported by the service implementation
   */
  def readEntity(id: IdType) : Option[EntityType] = throw new OperationNotSupported("readEntity")

  /**
   * Saves the specified Entity and returns the updated entity.
   * Returns None, if the entity does not exist.
   *
   * @param entity Entity to be saved (updated)
   *
   * @throws CRUDServiceError if the request could not be processed
   * @throws OperationNotSupported if this operation is not supported by the service implementation
   */
  def updateEntity(entity: EntityType) : Option[EntityType] = throw new OperationNotSupported("updateEntity")

  /**
   * Creates (persists) the specified entity. Returns the updated entity, if the operation was successful.
   * Returns None or throws an Exception if the Entity could not be created.
   *
   * @param entity Entity to be created
   *
   * @throws CRUDServiceError if the request could not be processed
   * @throws OperationNotSupported if this operation is not supported by the service implementation
   */
  def createEntity(entity: EntityType) : Option[EntityType] = throw new OperationNotSupported("createEntity")

  /**
   * Deletes the entity with the specified ID. Returns the ID of the deleted entity, or None if the entity does not exist.
   *
   * @param id ID of the entity to be deleted
   *
   * @throws CRUDServiceError if the request could not be processed
   * @throws OperationNotSupported if this operation is not supported by the service implementation
   */
  def deleteEntity(id: IdType) : Option[IdType] = throw new OperationNotSupported("deleteEntity")

  /**
   * Called for non-CRUD messages, ie messages not listed in [[CRUDService]].
   *
   * @param msg the message to be handled
   *
   * @throws CRUDServiceError if the request could not be processed
   * @throws OperationNotSupported if this operation is not supported by the service implementation
   */
  def otherMessage(msg: Any) : Unit = throw new OperationNotSupported("otherMessage")


  final override def process = {
    case ListEntities   if isRequest => request ! wrapListResponse( listEntities() )
    case ReadEntity(id) if isRequest => request ! readEntity(checkId(id))
    case UpdateEntity(e)             => if(isRequest) request ! updateEntity(checkEntity(e)) else updateEntity(checkEntity(e))
    case CreateEntity(e)             => if(isRequest) request ! createEntity(checkEntity(e)) else createEntity(checkEntity(e))
    case DeleteEntity(id)            => if(isRequest) request ! deleteEntity(checkId(id)) else deleteEntity((checkId(id)))
    case msg                         => otherMessage(msg)
  }
}


/**
 * Defines the message types for CRUD services.
 */
trait CRUDServiceMessages {

  /**
   * Request to return all entities.
   * <br/>
   * Response: Iterable with all entites, wrapped via [[CRUDService.wrapListResponse]]
   *
   * @see [[CRUDService.listEntities]], [[CRUDService.wrapListResponse]]
   */
  case object ListEntities


  /**
   * Request to read the entity with the specified ID.
   * <br/>
   * Response: ```Option[EntityType]```
   *
   * @param id The ID of the requested entity
   *
   * @see [[CRUDService.readEntity]]
   */
  case class ReadEntity(id: Any)

  /**
   * Request to update the specified entity.
   * <br/>
   * Repsonse: ```Some(entity)``` if the update was successful, ```None``` if
   * the entity to be updated does not exist.
   *
   * @param entity Entity to be updated
   *
   * @see [[CRUDService.updateEntity]]
   */
  case class UpdateEntity[EntityType](entity: EntityType)

  /**
   * Request to create the specified entity.
   * <br/>
   * Response: ```Some(entity)``` if the operation was successful.
   *
   * @param entity
   *
   * @see [[CRUDService.createEntity]]
   */
  case class CreateEntity[EntityType](entity: EntityType)

  /**
   * Request to delete the entity with the specified ID.
   * <br/>
   * Repsonse: ```Some(id)``` if the operation was successful.
   *
   * @param id
   *
   * @see [[CRUDService.deleteEntity]]
   */
  case class DeleteEntity(id: Any)

}

object CRUDService extends CRUDServiceMessages {
  class CRUDServiceError(msg: String, cause: Throwable) extends RuntimeException(msg,cause) {
    def this(msg: String) = this(msg,null)
    def this(cause: Throwable) = this(cause.toString,cause)
  }

  class OperationNotSupported(op: String) extends CRUDServiceError(s"CRUD Operation not supported: $op")

  class EntityNotExistent(id: Any, msg: String = "") extends CRUDServiceError(s"${msg}Entity with id=$id does not exist!")

  class InvalidId(id: Any) extends CRUDServiceError(s"Invalid id: $id")

  class InvalidEntity(entity: Any) extends CRUDServiceError(s"Invalid entity: $entity")
}
